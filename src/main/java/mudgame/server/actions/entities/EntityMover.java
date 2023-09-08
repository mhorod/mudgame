package mudgame.server.actions.entities;

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
        entityManager = new EntityManager(state.entityBoard(), state.fogOfWar());
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
            List<SingleMove> masked = maskedMoves(player, moves);
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

    private List<SingleMove> maskedMoves(PlayerID player, List<SingleMove> moves) {
        LinkedList<SingleMove> masked = new LinkedList<>();
        PlayerFogOfWar playerFow = fow(player);
        for (int i = 0; i < moves.size(); i++) {
            SingleMove m = moves.get(i);

            Position previous = i > 0 ? moves.get(i - 1).destinationNullable() : null;
            Position next = i < moves.size() - 1 ? moves.get(i + 1).destinationNullable() : null;
            Position current = m.destinationNullable();

            if (playerFow.isVisible(previous) || playerFow.isVisible(current) ||
                playerFow.isVisible(next))
                masked.add(m.withoutVisibilityChange());
            else
                masked.add(SingleMove.hidden());
        }

        while (!masked.isEmpty() && masked.get(0).isHidden())
            masked.removeFirst();
        while (!masked.isEmpty() && masked.get(masked.size() - 1).isHidden())
            masked.removeLast();
        return masked;
    }

    private PlayerFogOfWar fow(PlayerID player) {
        return state.fogOfWar().playerFogOfWar(player);
    }

    private List<SingleMove> getMoves(MoveEntity a) {
        List<SingleMove> result = new ArrayList<>();
        List<Position> path = pathfinder.findPath(a.entityID(), a.destination());

        for (Position next : path) {
            VisibilityChange visibilityChange = visibility.convert(
                    entityManager.moveEntity(a.entityID(), next));
            result.add(new SingleMove(next, visibilityChange));
        }
        return result;
    }
}
