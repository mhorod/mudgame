package core.entities;

import core.entities.model.Entity;
import core.entities.model.EntityData;
import core.entities.model.EntityType;
import core.model.EntityID;
import core.model.PlayerID;
import core.model.Position;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@EqualsAndHashCode
public final class EntityBoard implements EntityBoardView, Serializable {
    private final Map<Position, List<EntityID>> board = new HashMap<>();
    private final Map<EntityID, Position> entityPositions = new HashMap<>();
    private final Map<EntityID, Entity> entitiesById = new HashMap<>();
    private long nextEntityID = 0;

    public Entity createEntity(EntityData data, PlayerID owner, Position position) {
        Entity entity = new Entity(data, newEntityID(), owner);
        placeEntity(entity, position);
        return entity;
    }

    public Entity createEntity(EntityType type, PlayerID owner, Position position) {
        return createEntity(EntityData.ofType(type), owner, position);
    }

    @Override
    public List<Entity> allEntities() {
        return entitiesById.values().stream().toList();
    }

    @Override
    public List<Entity> entitiesAt(Position position) {
        return mutableEntitiesAt(position).stream().map(entitiesById::get).toList();
    }

    @Override
    public boolean containsEntity(EntityID entityID) {
        return entityPositions.containsKey(entityID);
    }

    @Override
    public Position entityPosition(EntityID entityID) {
        if (!containsEntity(entityID))
            throw new EntityDoesNotExist(entityID);
        return entityPositions.get(entityID);
    }

    public void removeEntity(EntityID entityID) {
        if (!containsEntity(entityID))
            throw new EntityDoesNotExist(entityID);

        Position position = entityPositions.get(entityID);
        board.get(position).remove(entityID);
        entityPositions.remove(entityID);
        entitiesById.remove(entityID);
    }


    public void placeEntity(Entity entity, Position position) {
        if (containsEntity(entity.id()))
            throw new EntityIsAlreadyPlaced(entity);

        mutableEntitiesAt(position).add(entity.id());
        entitiesById.put(entity.id(), entity);
        entityPositions.put(entity.id(), position);
    }

    public void moveEntity(EntityID entityID, Position targetPosition) {
        if (!containsEntity(entityID))
            throw new EntityDoesNotExist(entityID);

        Entity entity = entitiesById.get(entityID);
        removeEntity(entityID);
        placeEntity(entity, targetPosition);
    }

    @Override
    public Entity findEntityByID(EntityID entityID) {
        return entitiesById.get(entityID);
    }

    private EntityID newEntityID() {
        return new EntityID(nextEntityID++);
    }


    private List<EntityID> mutableEntitiesAt(Position position) {
        return board.computeIfAbsent(position, key -> new ArrayList<>());
    }
}
