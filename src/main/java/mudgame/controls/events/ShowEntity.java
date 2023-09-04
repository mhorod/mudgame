package mudgame.controls.events;

import core.entities.model.Entity;
import core.model.Position;
import mudgame.events.Event;

/**
 * Emitted when entity was previously hidden by a fog of war but it became visible.
 */
public record ShowEntity(Entity entity, Position position) implements Event {
}
