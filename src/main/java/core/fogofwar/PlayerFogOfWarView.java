package core.fogofwar;

import core.model.Position;

import java.util.List;

public interface PlayerFogOfWarView {
    boolean isVisible(Position position);
    List<Position> visiblePositions();
}
