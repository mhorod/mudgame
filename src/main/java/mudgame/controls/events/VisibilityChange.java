package mudgame.controls.events;

import core.entities.model.Entity;
import core.event.Event;
import core.model.Position;
import core.terrain.model.TerrainType;

import java.io.Serializable;
import java.util.List;

public record VisibilityChange(List<PositionVisibilityChange> positions)
        implements Event, Serializable {
    public static VisibilityChange of(PositionVisibilityChange... ps) {
        return new VisibilityChange(List.of(ps));
    }

    public sealed interface PositionVisibilityChange {
        Position position();
    }

    public record ShowPosition(
            Position position,
            TerrainType terrain,
            List<Entity> entities
    ) implements PositionVisibilityChange {
    }

    public record HidePosition(Position position) implements PositionVisibilityChange { }


    public static VisibilityChange empty() {
        return new VisibilityChange(List.of());
    }
}
