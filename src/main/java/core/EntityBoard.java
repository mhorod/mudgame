package core;

import core.entities.Entity;
import core.entities.EntityData;
import core.id.EntityID;
import core.id.PlayerID;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EntityBoard
{
    public static class EntityIsAlreadyPlaced extends RuntimeException
    {
        public EntityIsAlreadyPlaced(Entity entity)
        {
            super("Entity with id " + entity.id() + " is already placed.");
        }
    }

    public static class EntityDoesNotExist extends RuntimeException
    {
        public EntityDoesNotExist(EntityID entityID)
        {
            super("Entity with id " + entityID + " does not exist.");
        }
    }

    private final Map<Position, List<EntityID>> board = new HashMap<>();
    private final Map<EntityID, Position> entityPositions = new HashMap<>();
    private final Map<EntityID, Entity> entitiesById = new HashMap<>();
    private long nextEntityID = 0;

    public Entity createEntity(EntityData data, PlayerID owner, Position position)
    {
        EntityID entityID = newEntityID();
        Entity entity = new Entity(data, entityID, owner);
        placeEntity(entity, position);
        return entity;
    }

    public List<Entity> allEntities()
    {
        return entitiesById.values().stream().toList();
    }

    public List<Entity> entitiesAt(Position position)
    {
        return mutableEntitiesAt(position).stream().map(entitiesById::get).toList();
    }

    public Position entityPosition(EntityID entityID)
    {
        if (!containsEntity(entityID))
            throw new EntityDoesNotExist(entityID);
        return entityPositions.get(entityID);
    }

    public void removeEntity(EntityID entityID)
    {
        if (!containsEntity(entityID))
            throw new EntityDoesNotExist(entityID);

        Position position = entityPositions.get(entityID);
        board.get(position).remove(entityID);
        entityPositions.remove(entityID);
        entitiesById.remove(entityID);
    }

    public boolean containsEntity(EntityID entityID)
    {
        return entityPositions.containsKey(entityID);
    }

    public void placeEntity(Entity entity, Position position)
    {
        if (containsEntity(entity.id()))
            throw new EntityIsAlreadyPlaced(entity);

        mutableEntitiesAt(position).add(entity.id());
        entitiesById.put(entity.id(), entity);
        entityPositions.put(entity.id(), position);
    }

    public void moveEntity(EntityID entityID, Position targetPosition)
    {
        if (!containsEntity(entityID))
            throw new EntityDoesNotExist(entityID);

        Entity entity = entitiesById.get(entityID);
        removeEntity(entityID);
        placeEntity(entity, targetPosition);
    }


    private EntityID newEntityID()
    {
        return new EntityID(nextEntityID++);
    }


    private List<EntityID> mutableEntitiesAt(Position position)
    {
        return board.computeIfAbsent(position, key -> new ArrayList<>());
    }
}
