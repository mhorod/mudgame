package core.fogofwar.events;

import core.entities.model.Entity;
import mudgame.events.Event;
import core.model.Position;
import core.terrain.Terrain;

import java.io.Serializable;
import java.util.List;

public record VisibilityChange(List<PositionVisibilityChange> positions)
        implements Event, Serializable {
    public static VisibilityChange of(PositionVisibilityChange... ps) {
        return new VisibilityChange(List.of(ps));
    }

    public sealed interface PositionVisibilityChange { }

    public record ShowPosition(
            Position position,
            Terrain terrain,
            List<Entity> entities
    ) implements PositionVisibilityChange {
    }

    public record HidePosition(Position position) implements PositionVisibilityChange { }


    public static VisibilityChange empty() {
        return new VisibilityChange(List.of());
    }
}
