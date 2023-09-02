package core.entities;

import core.model.EntityID;

import java.util.HashMap;
import java.util.Map;

public class EntityMovementManager {
    private final Map<EntityID, Integer> movementMap = new HashMap<>();

    public int getTurnMovement(EntityID entityID) {
        return movementMap.getOrDefault(entityID, 0);
    }

    void setTurnMovement(EntityID entityID, int movement) {
        movementMap.put(entityID, movement);
    }

    void moveEntity(EntityID entityID, int movementTaken) {
        movementMap.put(entityID, movementMap.get(entityID) - movementTaken);
    }
}
