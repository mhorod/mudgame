package core.entities.events;

import mudgame.events.Event;
import core.model.EntityID;

public record HideEntity(EntityID entityID) implements Event {
}
