package core.fogofwar.events;

import core.events.Event;
import core.model.Position;

public record SetVisible(Position position, boolean isVisible) implements Event {
}
