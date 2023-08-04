package core;

import java.util.HashSet;
import java.util.Set;

public class SimpleFogOfWar implements FogOfWar
{
    private final Set<Position> visiblePositions = new HashSet<>();

    @Override
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
