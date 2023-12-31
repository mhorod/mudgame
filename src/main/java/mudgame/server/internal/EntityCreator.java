package mudgame.server.internal;

import core.claiming.ClaimChange;
import core.entities.model.Entity;
import core.entities.model.EntityType;
import core.fogofwar.PlayerFogOfWar.PositionVisibility;
import core.model.PlayerID;
import core.model.Position;
import lombok.RequiredArgsConstructor;
import mudgame.controls.events.VisibilityChange;
import mudgame.server.state.ServerState;

import java.util.Set;

@RequiredArgsConstructor
class EntityCreator {
    private final ServerState state;
    private final Visibility visibility;

    public CreatedEntity createEntity(EntityType type, PlayerID owner, Position position) {
        Entity entity = state.entityBoard().createEntity(type, owner, position);
        entity.getCost().ifPresent(r -> state.resourceManager().subtract(owner, r));

        Set<PositionVisibility> positions = state.fogOfWar()
                .playerFogOfWar(owner)
                .placeEntity(entity, position);
        VisibilityChange visibilityChange = visibility.get(positions);
        ClaimChange claimChange = state.claimedArea()
                .placeEntity(entity, position)
                .masked(state.terrain());
        return new CreatedEntity(entity, visibilityChange, claimChange);
    }

}
