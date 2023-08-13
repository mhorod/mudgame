package core.entities;

public class EntityIsAlreadyPlaced extends RuntimeException
{
    public EntityIsAlreadyPlaced(Entity entity)
    {
        super("Entity with id " + entity.id() + " is already placed.");
    }
}
