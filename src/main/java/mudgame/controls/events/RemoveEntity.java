package mudgame.controls.events;

import core.model.EntityID;
import mudgame.events.Event;

public record RemoveEntity(EntityID entityID) implements Event {
}
