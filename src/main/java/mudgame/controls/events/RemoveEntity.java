package mudgame.controls.events;

import core.event.Event;
import core.model.EntityID;

/**
 * Sent when entity should be removed from a client's board due to visibility.
 */
public record RemoveEntity(EntityID entityID) implements Event {
}
