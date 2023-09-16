package mudgame.controls.events;


import core.entities.model.Entity;
import core.model.Position;

/**
 * Emitted when an entity is placed under fog of war and will be seen due to subsequent events.
 */
public record PlaceEntity(Entity entity, Position position) implements Event {
}
