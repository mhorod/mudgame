package core;

import core.id.PlayerID;

public interface FogOfWarView
{
    boolean isVisible(Position position, PlayerID viewer);
}
