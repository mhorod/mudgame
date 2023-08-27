package core.entities.events;

import core.entities.model.Entity;
import core.events.model.Event;
import core.model.Position;

public record PlaceEntity(Entity entity, Position position) implements Event { }
