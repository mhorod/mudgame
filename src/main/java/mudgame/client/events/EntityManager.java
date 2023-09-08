package mudgame.client.events;

import core.entities.EntityBoard;
import core.entities.model.Entity;
import core.fogofwar.PlayerFogOfWar;
import core.model.EntityID;
import core.model.Position;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
class EntityManager {
    private final EntityBoard entityBoard;
    private final PlayerFogOfWar playerFogOfWar;

    void placeEntity(Entity entity, Position position) {
        entityBoard.placeEntity(entity, position);
        playerFogOfWar.placeEntity(entity, position);
    }

    void removeEntity(EntityID entityID) {
        entityBoard.removeEntity(entityID);
        playerFogOfWar.removeEntity(entityID);
    }

    void moveEntity(EntityID entityID, Position destination) {
        entityBoard.moveEntity(entityID, destination);
        playerFogOfWar.moveEntity(entityID, destination);
    }
}
