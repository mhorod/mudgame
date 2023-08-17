package core.entities.events;

import core.model.Position;
import core.entities.model.Entity;
import core.events.Event;

public record PlaceEntity(Entity entity, Position position) implements Event { }
