package mudgame.controls.events;

import core.event.Event;
import core.model.EntityID;

/**
 * Remove entity due to a loss of Health
 */
public record KillEntity(EntityID entityID) implements Event {
}
