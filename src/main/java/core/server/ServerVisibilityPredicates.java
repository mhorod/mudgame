package core.server;

import core.components.EventEntityBoard;
import core.fogofwar.FogOfWar;
import core.model.PlayerID;
import core.model.Position;
import lombok.RequiredArgsConstructor;

import java.util.function.Predicate;

@RequiredArgsConstructor
public final class ServerVisibilityPredicates implements EventEntityBoard.VisibilityPredicates {

    private final FogOfWar fogOfWar;

    @Override
    public Predicate<PlayerID> isVisible(Position position) {
        return id -> fogOfWar.isVisible(position, id);
    }

    @Override
    public Predicate<PlayerID> isMoveVisible(Position from, Position to) {
        return id -> fogOfWar.isVisible(from, id) || fogOfWar.isVisible(to, id);
    }
}
