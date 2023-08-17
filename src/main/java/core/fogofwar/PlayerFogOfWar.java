package core.fogofwar;

import core.Position;
import lombok.EqualsAndHashCode;

import java.util.HashSet;
import java.util.Set;

@EqualsAndHashCode
public class PlayerFogOfWar implements PlayerFogOfWarView
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
