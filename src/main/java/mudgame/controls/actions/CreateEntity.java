package mudgame.controls.actions;

import core.entities.model.EntityType;
import core.event.Action;
import core.model.PlayerID;
import core.model.Position;

public record CreateEntity(EntityType type, PlayerID owner, Position position)
        implements Action {
}
