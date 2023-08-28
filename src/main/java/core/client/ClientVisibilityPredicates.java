package core.client;

import core.components.EventEntityBoard;
import core.model.PlayerID;
import core.model.Position;
import lombok.RequiredArgsConstructor;

import java.util.function.Predicate;

@RequiredArgsConstructor
final class ClientVisibilityPredicates implements EventEntityBoard.VisibilityPredicates {

    private final PlayerID playerID;

    @Override
    public Predicate<PlayerID> isVisible(Position position) {
        // We assume that events generated locally are always seen by the local player
        return playerID::equals;
    }

    @Override
    public Predicate<PlayerID> isMoveVisible(Position from, Position to) {
        // We assume that events generated locally are always seen by the local player
        return playerID::equals;
    }
}
