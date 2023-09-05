package mudgame.controls.events;

import core.entities.model.Entity;
import core.event.Event;
import core.model.Position;

/**
 * Emitted when a brand-new entity is spawned on the board.
 * {@link #visibilityChange()} contains fog of war info if the receiver owns the entity.
 */
public record SpawnEntity(
        Entity entity,
        Position position,
        VisibilityChange visibilityChange
) implements Event { }
