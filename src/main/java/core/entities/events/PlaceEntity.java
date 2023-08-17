package core.entities.events;

import core.Position;
import core.entities.Entity;
import core.events.Event;

public record PlaceEntity(Entity entity, Position position) implements Event { }
