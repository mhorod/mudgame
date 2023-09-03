package core.entities;

import core.model.EntityID;
import core.model.PlayerID;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import static java.util.stream.Collectors.toMap;

public class EntityMovementManager {
    private final Map<EntityID, Integer> movementMap;

    public EntityMovementManager() {
        this.movementMap = new HashMap<>();
    }

    private EntityMovementManager(Map<EntityID, Integer> movementMap) {
        this.movementMap = movementMap;
    }

    public int getTurnMovement(EntityID entityID) {
        return movementMap.getOrDefault(entityID, 0);
    }

    void setTurnMovement(EntityID entityID, int movement) {
        movementMap.put(entityID, movement);
    }

    void moveEntity(EntityID entityID, int movementTaken) {
        movementMap.put(entityID, movementMap.get(entityID) - movementTaken);
    }

    public EntityMovementManager ofPlayer(PlayerID playerID, EntityBoard entityBoard) {
        Map<EntityID, Integer> newMovementMap = movementMap.entrySet().stream()
                .filter(e -> entityBoard.entityOwner(e.getKey()).equals(playerID))
                .collect(toMap(Entry::getKey, Entry::getValue));
        return new EntityMovementManager(newMovementMap);
    }
}
