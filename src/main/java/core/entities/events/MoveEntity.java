package core.entities.events;

import core.events.Event.Action;
import core.model.EntityID;
import core.model.Position;

public record MoveEntity(EntityID entityID, Position destination) implements Action { }
