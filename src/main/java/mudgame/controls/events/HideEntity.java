package mudgame.controls.events;

import core.model.EntityID;
import mudgame.events.Event;

public record HideEntity(EntityID entityID) implements Event {
}
