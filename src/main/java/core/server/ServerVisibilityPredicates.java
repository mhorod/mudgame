package core.server;

import core.EventEntityBoard;
import core.fogofwar.FogOfWarView;
import core.model.PlayerID;
import core.model.Position;
import lombok.RequiredArgsConstructor;

import java.util.function.Predicate;

@RequiredArgsConstructor
public class ServerVisibilityPredicates implements EventEntityBoard.VisibilityPredicates {

    private final FogOfWarView fogOfWar;

    @Override
    public Predicate<PlayerID> isVisible(Position position) {
        return id -> fogOfWar.isVisible(position, id);
    }

    @Override
    public Predicate<PlayerID> isMoveVisible(Position from, Position to) {
        return id -> fogOfWar.isVisible(from, id) || fogOfWar.isVisible(to, id);
    }
}
