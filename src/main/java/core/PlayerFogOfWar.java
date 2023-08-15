package core;

import java.util.HashSet;
import java.util.Set;

public class PlayerFogOfWar implements FogOfWarView
{
    private final Set<Position> visiblePositions = new HashSet<>();

    public void setVisibility(Position position, boolean isVisible)
    {
        if (isVisible)
            visiblePositions.add(position);
        else
            visiblePositions.remove(position);
    }

    @Override
    public boolean isVisible(Position position)
    {
        return visiblePositions.contains(position);
    }
}
