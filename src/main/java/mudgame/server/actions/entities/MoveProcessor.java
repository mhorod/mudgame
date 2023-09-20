package mudgame.server.actions.entities;

import core.claiming.ClaimChange;
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
import mudgame.server.actions.EventSender;
import mudgame.server.internal.InteractiveState;
import mudgame.server.internal.MovedEntity;

import java.util.List;
import java.util.stream.IntStream;


final class MoveProcessor {
    private final InteractiveState state;
    private final EventSender sender;
    private final Pathfinder pathfinder;

    public MoveProcessor(InteractiveState state, EventSender sender) {
        this.state = state;
        this.sender = sender;
        this.pathfinder = state.pathfinder();
    }

    public void moveEntity(MoveEntity a) {
        List<SingleMove> moves = getMoves(a.entityID(), a.destination());
        state.players().forEach(player -> sendMovesTo(player, moves, a.entityID()));
    }

    private void sendMovesTo(PlayerID player, List<SingleMove> moves, EntityID entityID) {
        if (player.equals(state.entityOwner(entityID)))
            sendMovesToOwner(player, entityID, moves);
        else
            sendMovesToOther(player, entityID, moves);
    }

    private void sendMovesToOwner(PlayerID player, EntityID entityID, List<SingleMove> moves) {
        sender.send(new MoveEntityAlongPath(entityID, moves), player);
    }

    private void sendMovesToOther(PlayerID player, EntityID entityID, List<SingleMove> moves) {
        List<SingleMove> masked = masked(player, moves);
        if (!masked.isEmpty()) {
            prepare(player, entityID, masked);
            sender.send(new MoveEntityAlongPath(entityID, masked), player);
            cleanup(player, entityID, masked);
        }
    }

    private void prepare(PlayerID player, EntityID entityID, List<SingleMove> masked) {
        Position start = masked.get(0).destinationNullable();
        if (!state.playerSees(player, start))
            sender.send(new PlaceEntity(state.findEntityByID(entityID), start), player);
    }

    private void cleanup(PlayerID player, EntityID entityID, List<SingleMove> masked) {
        Position end = masked.get(masked.size() - 1).destinationNullable();
        if (!state.playerSees(player, end))
            sender.send(new RemoveEntity(entityID), player);
    }

    private List<SingleMove> masked(PlayerID player, List<SingleMove> moves) {
        return stripEnds(maskedMoves(player, moves));
    }

    private List<SingleMove> maskedMoves(PlayerID player, List<SingleMove> moves) {
        return IntStream.range(0, moves.size())
                .mapToObj(i -> maskedIthMove(player, i, moves))
                .toList();
    }

    private SingleMove maskedIthMove(PlayerID player, int i, List<SingleMove> moves) {
        if (shouldSeeDestination(player, i, moves))
            return shown(player, moves.get(i));
        else
            return hidden(player, moves.get(i));
    }

    private SingleMove hidden(PlayerID player, SingleMove move) {
        ClaimChange maskedClaimChange = state.maskedFor(player, move.claimChange());
        return new SingleMove(null, VisibilityChange.empty(), maskedClaimChange);
    }

    private SingleMove shown(PlayerID player, SingleMove move) {
        Position destination = move.destinationNullable();
        ClaimChange maskedClaimChange = state.maskedFor(player, move.claimChange());
        return new SingleMove(destination, VisibilityChange.empty(), maskedClaimChange);
    }

    private boolean shouldSeeDestination(PlayerID player, int i, List<SingleMove> moves) {
        Position previous = i > 0 ? moves.get(i - 1).destinationNullable() : null;
        Position next = i < moves.size() - 1 ? moves.get(i + 1).destinationNullable() : null;
        Position current = moves.get(i).destinationNullable();
        return state.playerSeesAny(player, previous, next, current);
    }


    private List<SingleMove> stripEnds(List<SingleMove> result) {
        return result.stream()
                .dropWhile(SingleMove::isHidden)
                .takeWhile(SingleMove::isShown)
                .toList();
    }

    private List<SingleMove> getMoves(EntityID entityID, Position destination) {
        return pathfinder.findPath(entityID, destination)
                .stream()
                .map(p -> moveEntity(entityID, p))
                .toList();
    }

    private SingleMove moveEntity(EntityID entityID, Position destination) {
        MovedEntity movedEntity = state.moveEntity(entityID, destination);
        return new SingleMove(
                destination,
                movedEntity.visibilityChange(),
                movedEntity.claimChange()
        );
    }
}
