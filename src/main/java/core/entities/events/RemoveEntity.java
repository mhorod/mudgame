package core.entities.events;

import core.events.Event.Action;
import core.model.EntityID;

public record RemoveEntity(EntityID entityID) implements Action { }
