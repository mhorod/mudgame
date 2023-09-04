package mudgame.controls.events;

import core.model.EntityID;
import core.event.Event;

public record RemoveEntity(EntityID entityID) implements Event {
}
