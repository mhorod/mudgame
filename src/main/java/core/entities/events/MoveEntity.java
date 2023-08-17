package core.entities.events;

import core.Position;
import core.events.Event.Action;
import core.id.EntityID;

public record MoveEntity(EntityID entityID, Position destination) implements Action { }
