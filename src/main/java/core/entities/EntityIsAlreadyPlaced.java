package core.entities;

import core.entities.model.Entity;

public class EntityIsAlreadyPlaced extends RuntimeException
{
    public EntityIsAlreadyPlaced(Entity entity)
    {
        super("Entity with id " + entity.id() + " is already placed.");
    }
}
