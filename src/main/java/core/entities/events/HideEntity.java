package core.entities.events;

import core.events.Event;
import core.model.EntityID;

public record HideEntity(EntityID entityID) implements Event {
}
