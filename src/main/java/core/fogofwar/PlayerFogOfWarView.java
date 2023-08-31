package core.fogofwar;

import core.model.Position;

import java.util.Set;

public interface PlayerFogOfWarView {
    boolean isVisible(Position position);

    Set<Position> visiblePositions();
}
