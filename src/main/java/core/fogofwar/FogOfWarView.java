package core.fogofwar;

import core.model.Position;
import core.model.PlayerID;

public interface FogOfWarView
{
    boolean isVisible(Position position, PlayerID viewer);
}
