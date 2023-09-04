package mudgame.controls.events;

import core.entities.model.Entity;
import core.fogofwar.events.VisibilityChange;
import core.model.Position;
import mudgame.events.Event;

/**
 * Emitted when a brand-new entity is spawned on the board.
 * {@link #visibilityChange()} contains fog of war info if the receiver owns the entity.
 */
public record SpawnEntity(
        Entity entity,
        Position position,
        VisibilityChange visibilityChange
) implements Event { }
