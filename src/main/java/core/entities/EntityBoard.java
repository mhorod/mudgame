package core.entities;

import core.entities.model.Entity;
import core.entities.model.EntityData;
import core.model.EntityID;
import core.model.PlayerID;
import core.model.Position;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public final class EntityBoard implements EntityBoardView, Serializable {
    private final Map<Position, List<EntityID>> board = new HashMap<>();
    private final Map<EntityID, Position> entityPositions = new HashMap<>();
    private final Map<EntityID, Entity> entitiesById = new HashMap<>();
    private long nextEntityID = 0;

    public Entity createEntity(EntityData data, PlayerID owner, Position position) {
        EntityID entityID = newEntityID();
        Entity entity = new Entity(data, entityID, owner);
        placeEntity(entity, position);
        return entity;
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

    @Override
    public boolean containsEntity(EntityID entityID) {
        return entityPositions.containsKey(entityID);
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

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        EntityBoard that = (EntityBoard) o;
        return board.equals(that.board) && entityPositions.equals(that.entityPositions) &&
               entitiesById.equals(that.entitiesById);
    }

    @Override
    public int hashCode() {
        return Objects.hash(board, entityPositions, entitiesById);
    }
}
