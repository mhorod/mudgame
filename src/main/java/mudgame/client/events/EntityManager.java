package mudgame.client.events;

import core.entities.EntityBoard;
import core.entities.model.Entity;
import core.fogofwar.PlayerFogOfWar;
import core.model.EntityID;
import core.model.PlayerID;
import core.model.Position;
import core.terrain.TerrainView;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
class EntityManager {
    private final PlayerID myPlayerID;
    private final EntityBoard entityBoard;
    private final PlayerFogOfWar playerFogOfWar;
    private final TerrainView terrain;

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
        if (entityBoard.entityOwner(entityID).equals(myPlayerID)) {
            entityBoard.findEntityByID(entityID)
                    .getMovement()
                    .ifPresent(m -> m.move(terrain.terrainAt(destination).getMovementCost()));
        }
    }

    public void damageEntity(EntityID entityID, int damage) {
        entityBoard.findEntityByID(entityID).damage(damage);
    }
}
