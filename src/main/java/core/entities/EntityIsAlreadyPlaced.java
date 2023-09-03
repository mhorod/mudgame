package core.entities;

import core.entities.model.Entity;

public final class EntityIsAlreadyPlaced extends RuntimeException {
    public EntityIsAlreadyPlaced(Entity entity) {
        super("Entity with entityID " + entity.id() + " is already placed.");
    }
}
