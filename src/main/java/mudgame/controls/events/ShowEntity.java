package core.entities.events;

import core.entities.model.Entity;
import mudgame.events.Event;
import core.model.Position;

/**
 * Emitted when entity was previously hidden by a fog of war but it became visible.
 */
public record ShowEntity(Entity entity, Position position) implements Event {
}
