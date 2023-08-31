package core.entities;

import core.model.EntityID;

public final class EntityDoesNotExist extends RuntimeException {
    public EntityDoesNotExist(EntityID entityID) {
        super("Entity with id " + entityID + " does not exist.");
    }
}
