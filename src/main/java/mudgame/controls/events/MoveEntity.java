package mudgame.controls.events;

import core.model.EntityID;
import core.model.Position;
import core.event.Action;

public record MoveEntity(EntityID entityID, Position destination) implements Action {
}
