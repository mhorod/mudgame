package core.fogofwar;

import core.Position;
import core.id.PlayerID;

public interface FogOfWarView
{
    boolean isVisible(Position position, PlayerID viewer);
}
