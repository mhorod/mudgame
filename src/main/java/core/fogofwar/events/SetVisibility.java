package core.fogofwar.events;

import core.events.Event;
import core.model.Position;

import java.io.Serializable;
import java.util.List;

public record SetVisibility(List<SetPositionVisibility> positions) implements Event, Serializable {
    public static SetVisibility of(SetPositionVisibility... ps) {
        return new SetVisibility(List.of(ps));
    }

    public record SetPositionVisibility(Position position, boolean isVisible)
            implements Serializable { }

}
