package mudgame.controls.events;

import core.entities.model.EntityType;
import core.model.PlayerID;
import core.model.Position;
import core.event.Action;

public record CreateEntity(EntityType type, PlayerID owner, Position position)
        implements Action {
}
