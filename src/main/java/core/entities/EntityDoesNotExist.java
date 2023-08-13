package core.entities;

import core.id.EntityID;

public class EntityDoesNotExist extends RuntimeException
{
    public EntityDoesNotExist(EntityID entityID)
    {
        super("Entity with id " + entityID + " does not exist.");
    }
}
