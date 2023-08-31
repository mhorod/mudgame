package core.fogofwar;

import core.model.Position;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

@EqualsAndHashCode
public final class PlayerFogOfWar implements PlayerFogOfWarView, Serializable {
    private final Set<Position> visiblePositions = new HashSet<>();

    public void setVisibility(Position position, boolean isVisible) {
        if (isVisible)
            visiblePositions.add(position);
        else
            visiblePositions.remove(position);
    }

    @Override
    public boolean isVisible(Position position) {
        return visiblePositions.contains(position);
    }

    @Override
    public Set<Position> visiblePositions() {
        return Collections.unmodifiableSet(visiblePositions);
    }
}
