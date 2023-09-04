package mudgame.controls.events;

import core.model.EntityID;
import core.event.Event;

public record HideEntity(EntityID entityID) implements Event {
}
