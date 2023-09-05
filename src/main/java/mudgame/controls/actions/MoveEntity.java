package mudgame.controls.actions;

import core.event.Action;
import core.model.EntityID;
import core.model.Position;

public record MoveEntity(EntityID entityID, Position destination) implements Action {
}
