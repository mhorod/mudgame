package mudgame.server.actions.entities;

import core.claiming.ClaimedArea;
import core.claiming.ClaimedAreaView.ClaimChange;
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
    private final ClaimedArea claimedArea;

    record CreatedEntity(
            Entity entity,
            Set<PositionVisibility> changedPositions,
            ClaimChange claimChange
    ) { }

    record MovedEntity(
            EntityID entityID,
            Set<PositionVisibility> changedPositions,
            ClaimChange claimChange
    ) { }

    record RemovedEntity(
            EntityID entityID,
            Set<PositionVisibility> changedPositions,
            ClaimChange claimChange) { }

    CreatedEntity createEntity(EntityType type, PlayerID owner, Position position) {
        Entity entity = entityBoard.createEntity(type, owner, position);
        Set<PositionVisibility> positions = fow.playerFogOfWar(owner).placeEntity(entity, position);
        ClaimChange claimChange = claimedArea.placeEntity(entity, position);
        return new CreatedEntity(entity, positions, claimChange);
    }

    MovedEntity moveEntity(EntityID entityID, Position position) {
        PlayerID owner = entityBoard.entityOwner(entityID);
        entityBoard.moveEntity(entityID, position);
        ClaimChange claimChange = claimedArea.moveEntity(entityBoard.findEntityByID(entityID),
                                                         position);
        Set<PositionVisibility> positions = fow.playerFogOfWar(owner)
                .moveEntity(entityID, position);
        return new MovedEntity(entityID, positions, claimChange);
    }

    RemovedEntity removeEntity(EntityID entityID) {
        PlayerID owner = entityBoard.entityOwner(entityID);
        entityBoard.removeEntity(entityID);
        ClaimChange claimChange = claimedArea.removeEntity(entityID);
        Set<PositionVisibility> positions = fow.playerFogOfWar(owner).removeEntity(entityID);
        return new RemovedEntity(entityID, positions, claimChange);
    }
}