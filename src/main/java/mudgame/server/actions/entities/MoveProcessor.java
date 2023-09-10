package mudgame.server.actions.entities;

import core.claiming.ClaimedAreaView.ClaimChange;
import core.model.EntityID;
import core.model.PlayerID;
import core.model.Position;
import core.pathfinder.Pathfinder;
import mudgame.controls.actions.MoveEntity;
import mudgame.controls.events.MoveEntityAlongPath;
import mudgame.controls.events.MoveEntityAlongPath.SingleMove;
import mudgame.controls.events.PlaceEntity;
import mudgame.controls.events.RemoveEntity;
import mudgame.controls.events.VisibilityChange;
import mudgame.server.actions.Sender;
import mudgame.server.internal.EntityMover.MovedEntity;
import mudgame.server.internal.InteractiveState;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;


public class MoveProcessor {
    private final InteractiveState state;
    private final Sender sender;
    private final Pathfinder pathfinder;

    public MoveProcessor(InteractiveState state, Sender sender) {
        this.state = state;
        this.sender = sender;
        this.pathfinder = state.pathfinder();
    }


    public void moveEntity(MoveEntity a) {
        List<SingleMove> moves = getMoves(a);
        for (PlayerID player : state.players())
            sendMovesTo(player, moves, a.entityID());
    }

    private void sendMovesTo(PlayerID player, List<SingleMove> moves, EntityID entityID) {
        PlayerID owner = state.entityOwner(entityID);
        if (player.equals(owner)) {
            sender.send(new MoveEntityAlongPath(entityID, moves), player);
        } else {
            List<SingleMove> masked = masked(player, moves);
            if (!masked.isEmpty()) {
                Position start = masked.get(0).destinationNullable();
                Position end = masked.get(masked.size() - 1).destinationNullable();
                if (!state.playerSees(player, start))
                    sender.send(new PlaceEntity(state.findEntityByID(entityID), start), player);
                sender.send(new MoveEntityAlongPath(entityID, masked), player);
                if (!state.playerSees(player, end))
                    sender.send(new RemoveEntity(entityID), player);

            }
        }
    }

    private List<SingleMove> masked(PlayerID player, List<SingleMove> moves) {
        LinkedList<SingleMove> result = new LinkedList<>();
        for (int i = 0; i < moves.size(); i++) {
            SingleMove m = moves.get(i);

            Position previous = i > 0 ? moves.get(i - 1).destinationNullable() : null;
            Position next = i < moves.size() - 1 ? moves.get(i + 1).destinationNullable() : null;
            Position current = m.destinationNullable();

            ClaimChange maskedClaimChange = state.maskedFor(player, m.claimChange());

            if (state.playerSeesAny(player, previous, next, current))
                result.add(new SingleMove(m.destinationNullable(), VisibilityChange.empty(),
                                          maskedClaimChange));
            else
                result.add(SingleMove.hidden(maskedClaimChange));
        }

        while (!result.isEmpty() && result.get(0).isHidden())
            result.removeFirst();
        while (!result.isEmpty() && result.get(result.size() - 1).isHidden())
            result.removeLast();
        return result;
    }

    private List<SingleMove> getMoves(MoveEntity a) {
        List<SingleMove> result = new ArrayList<>();
        List<Position> path = pathfinder.findPath(a.entityID(), a.destination());

        for (Position next : path) {
            MovedEntity movedEntity = state.moveEntity(a.entityID(), next);
            result.add(new SingleMove(next, movedEntity.visibilityChange(),
                                      movedEntity.claimChange()));
        }
        return result;
    }

}
