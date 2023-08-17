package core.entities.events;

import core.events.Event.Action;
import core.id.EntityID;

public record RemoveEntity(EntityID entityID) implements Action { }
