package core.entities;

import core.entities.model.Entity;
import core.entities.model.EntityData;
import core.entities.model.EntityType;
import core.fogofwar.PlayerFogOfWar;
import core.model.EntityID;
import core.model.PlayerID;
import core.model.Position;
import lombok.EqualsAndHashCode;
import org.apache.commons.lang3.SerializationUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@EqualsAndHashCode
public final class EntityBoard implements EntityBoardView, Serializable {
    private final Map<Position, List<EntityID>> board = new HashMap<>();
    private final Map<EntityID, Position> entityPositions = new HashMap<>();
    private final Map<EntityID, Entity> entitiesById = new HashMap<>();
    @EqualsAndHashCode.Exclude
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
        if (board.containsKey(position))
            return board.get(position).stream().map(this::findEntityByID).toList();
        else
            return List.of();
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

    @Override
    public PlayerID entityOwner(EntityID entityID) {
        if (!containsEntity(entityID))
            throw new EntityDoesNotExist(entityID);
        return findEntityByID(entityID).owner();
    }

    public void removeEntity(EntityID entityID) {
        if (!containsEntity(entityID))
            throw new EntityDoesNotExist(entityID);


        Position position = entityPositions.get(entityID);
        board.get(position).remove(entityID);
        if (board.get(position).isEmpty())
            board.remove(position);
        entityPositions.remove(entityID);
        entitiesById.remove(entityID);
    }


    public void placeEntity(Entity entity, Position position) {
        if (containsEntity(entity.id()))
            throw new EntityIsAlreadyPlaced(entity);
        if (entity.id().id() >= nextEntityID)
            nextEntityID = entity.id().id() + 1;

        safeEntitiesAt(position).add(entity.id());
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


    private List<EntityID> safeEntitiesAt(Position position) {
        return board.computeIfAbsent(position, key -> new ArrayList<>());
    }

    public EntityBoard applyFogOfWar(PlayerFogOfWar fow) {
        EntityBoard newEntityBoard = new EntityBoard();
        for (Entity entity : allEntities()) {
            Position position = entityPosition(entity.id());
            if (fow.isVisible(position))
                newEntityBoard.placeEntity(SerializationUtils.clone(entity), position);
        }
        return newEntityBoard;
    }


    public Set<Position> occupiedPositions() {
        return board.keySet();
    }
}
