package mudgame.server.actions.entities;

import core.claiming.ClaimedAreaView.ClaimChange;
import core.entities.model.Entity;
import core.fogofwar.PlayerFogOfWar;
import core.model.EntityID;
import core.model.PlayerID;
import core.model.Position;
import core.pathfinder.EntityPathfinder;
import core.pathfinder.Pathfinder;
import mudgame.controls.actions.MoveEntity;
import mudgame.controls.events.MoveEntityAlongPath;
import mudgame.controls.events.MoveEntityAlongPath.SingleMove;
import mudgame.controls.events.PlaceEntity;
import mudgame.controls.events.RemoveEntity;
import mudgame.controls.events.VisibilityChange;
import mudgame.server.ServerGameState;
import mudgame.server.actions.Sender;
import mudgame.server.actions.entities.EntityManager.MovedEntity;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class EntityMover {
    private final ServerGameState state;
    private final Sender sender;
    private final EntityManager entityManager;
    private final Visibility visibility;
    private final Pathfinder pathfinder;

    public EntityMover(ServerGameState state, Sender sender) {
        this.state = state;
        this.sender = sender;
        entityManager = new EntityManager(
                state.entityBoard(),
                state.fogOfWar(),
                state.claimedArea()
        );
        visibility = new Visibility(state.entityBoard(), state.terrain());
        pathfinder = new EntityPathfinder(
                state.terrain(),
                state.entityBoard(),
                state.fogOfWar()
        );

    }


    public void moveEntity(MoveEntity a) {
        List<SingleMove> moves = getMoves(a);
        for (PlayerID player : players())
            sendMovesTo(player, moves, a.entityID());
    }

    private List<PlayerID> players() {
        return state.playerManager().getPlayerIDs();
    }

    private void sendMovesTo(PlayerID player, List<SingleMove> moves, EntityID entityID) {
        PlayerID owner = state.entityBoard().entityOwner(entityID);
        if (player.equals(owner)) {
            sender.send(new MoveEntityAlongPath(entityID, moves), player);
        } else {
            List<SingleMove> masked = masked(player, moves);
            if (!masked.isEmpty()) {
                Position start = masked.get(0).destinationNullable();
                Position end = masked.get(masked.size() - 1).destinationNullable();
                if (!fow(player).isVisible(start))
                    sender.send(new PlaceEntity(entity(entityID), start), player);
                sender.send(new MoveEntityAlongPath(entityID, masked), player);
                if (!fow(player).isVisible(end))
                    sender.send(new RemoveEntity(entityID), player);

            }
        }
    }

    private Entity entity(EntityID entityID) {
        return state.entityBoard().findEntityByID(entityID);
    }

    private List<SingleMove> masked(PlayerID player, List<SingleMove> moves) {
        LinkedList<SingleMove> result = new LinkedList<>();
        PlayerFogOfWar playerFow = fow(player);
        for (int i = 0; i < moves.size(); i++) {
            SingleMove m = moves.get(i);

            Position previous = i > 0 ? moves.get(i - 1).destinationNullable() : null;
            Position next = i < moves.size() - 1 ? moves.get(i + 1).destinationNullable() : null;
            Position current = m.destinationNullable();

            if (playerFow.isVisible(previous) || playerFow.isVisible(current) ||
                playerFow.isVisible(next))
                result.add(m.withoutVisibilityChange());
            else
                result.add(SingleMove.hidden(masked(player, m.claimChange())));
        }

        while (!result.isEmpty() && result.get(0).isHidden())
            result.removeFirst();
        while (!result.isEmpty() && result.get(result.size() - 1).isHidden())
            result.removeLast();
        return result;
    }

    private ClaimChange masked(PlayerID player, ClaimChange claimChange) {
        return claimChange.applyFogOfWar(fow(player));
    }

    private PlayerFogOfWar fow(PlayerID player) {
        return state.fogOfWar().playerFogOfWar(player);
    }

    private List<SingleMove> getMoves(MoveEntity a) {
        List<SingleMove> result = new ArrayList<>();
        List<Position> path = pathfinder.findPath(a.entityID(), a.destination());

        for (Position next : path) {
            MovedEntity movedEntity = entityManager.moveEntity(a.entityID(), next);
            VisibilityChange visibilityChange = visibility.convert(movedEntity.changedPositions());
            result.add(new SingleMove(next, visibilityChange, movedEntity.claimChange()));
        }
        return result;
    }
}
