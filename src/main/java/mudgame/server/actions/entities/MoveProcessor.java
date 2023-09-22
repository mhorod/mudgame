package mudgame.server.actions.entities;

import core.claiming.ClaimChange;
import core.model.EntityID;
import core.model.PlayerID;
import core.model.Position;
import core.pathfinder.Pathfinder;
import mudgame.controls.actions.MoveEntity;
import mudgame.controls.events.ClaimChanges;
import mudgame.controls.events.MoveEntityAlongPath;
import mudgame.controls.events.MoveEntityAlongPath.SingleMove;
import mudgame.controls.events.PlaceEntity;
import mudgame.controls.events.RemoveEntity;
import mudgame.controls.events.VisibilityChange;
import mudgame.server.actions.EventSender;
import mudgame.server.internal.InteractiveState;
import mudgame.server.internal.MovedEntity;

import java.util.ArrayList;
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
        List<SingleMove> stripped = stripEnds(masked);
        if (stripped.isEmpty()) {
            ClaimChanges claimChanges = claimChanges(player, moves);
            if (!claimChanges.claimChanges().isEmpty())
                sender.send(claimChanges, player);
        } else {
            prepare(player, entityID, masked, stripped);
            sender.send(new MoveEntityAlongPath(entityID, stripped), player);
            cleanup(player, entityID, masked, stripped);
        }
    }

    private ClaimChanges claimChanges(PlayerID player, List<SingleMove> moves) {
        List<ClaimChange> changes = moves.stream()
                .map(SingleMove::claimChange)
                .map(c -> c.masked(state.playerFow(player), state.terrain()))
                .filter(c -> !c.isEmpty())
                .toList();
        return new ClaimChanges(changes);
    }

    private void prepare(
            PlayerID player, EntityID entityID, List<SingleMove> masked, List<SingleMove> stripped
    ) {
        Position start = stripped.get(0).destinationNullable();
        List<ClaimChange> claimChanges = masked.stream()
                .takeWhile(SingleMove::isHidden)
                .map(SingleMove::claimChange)
                .filter(c -> !c.isEmpty())
                .toList();

        if (!state.playerSees(player, start)) {
            if (!claimChanges.isEmpty())
                sender.send(new ClaimChanges(claimChanges), player);
            sender.send(new PlaceEntity(state.findEntityByID(entityID), start), player);
        }
    }

    private void cleanup(
            PlayerID player, EntityID entityID, List<SingleMove> masked,
            List<SingleMove> stripped
    ) {
        int endIndex = masked.size() - 1;
        while (endIndex > 0 && masked.get(endIndex).isHidden())
            endIndex--;

        List<ClaimChange> claimChanges = masked.subList(endIndex + 1, masked.size())
                .stream().map(SingleMove::claimChange)
                .filter(c -> !c.isEmpty())
                .toList();

        Position end = stripped.get(stripped.size() - 1).destinationNullable();

        if (!state.playerSees(player, end)) {
            if (!claimChanges.isEmpty())
                sender.send(new ClaimChanges(claimChanges), player);
            sender.send(new RemoveEntity(entityID), player);
        }
    }

    private List<SingleMove> masked(PlayerID player, List<SingleMove> moves) {
        return maskedMoves(player, moves);
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
        int begin = 0;
        while (begin < result.size() && result.get(begin).isHidden())
            begin++;

        int end = result.size() - 1;
        while (end > 0 && result.get(end).isHidden())
            end--;

        end++;
        if (end <= begin)
            return List.of();

        return new ArrayList<>(result.subList(begin, end));
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
