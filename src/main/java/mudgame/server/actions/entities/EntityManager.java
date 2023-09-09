package mudgame.server.actions.entities;

import core.entities.EntityBoard;
import core.entities.model.Entity;
import core.entities.model.EntityType;
import core.fogofwar.FogOfWar;
import core.fogofwar.PlayerFogOfWar.PositionVisibility;
import core.model.EntityID;
import core.model.PlayerID;
import core.model.Position;
import lombok.RequiredArgsConstructor;

import java.util.Set;

@RequiredArgsConstructor
class EntityManager {
    private final EntityBoard entityBoard;
    private final FogOfWar fow;

    record CreatedEntity(Entity entity, Set<PositionVisibility> changedPositions) { }

    CreatedEntity createEntity(EntityType type, PlayerID owner, Position position) {
        Entity entity = entityBoard.createEntity(type, owner, position);
        Set<PositionVisibility> positions = fow.playerFogOfWar(owner).placeEntity(entity, position);
        return new CreatedEntity(entity, positions);
    }

    Set<PositionVisibility> moveEntity(EntityID entityID, Position position) {
        PlayerID owner = entityBoard.entityOwner(entityID);
        entityBoard.moveEntity(entityID, position);
        return fow.playerFogOfWar(owner).moveEntity(entityID, position);
    }

    Set<PositionVisibility> removeEntity(EntityID entityID) {
        PlayerID owner = entityBoard.entityOwner(entityID);
        entityBoard.removeEntity(entityID);
        return fow.playerFogOfWar(owner).removeEntity(entityID);
    }
}