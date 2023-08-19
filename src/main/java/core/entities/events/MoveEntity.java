package core.entities.events;

import core.model.Position;
import core.events.Event.Action;
import core.model.EntityID;

public record MoveEntity(EntityID entityID, Position destination) implements Action { }
