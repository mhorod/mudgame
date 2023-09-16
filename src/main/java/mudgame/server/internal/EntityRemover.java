package mudgame.server.internal;

import core.claiming.ClaimChange;
import core.fogofwar.PlayerFogOfWar.PositionVisibility;
import core.model.EntityID;
import core.model.PlayerID;
import lombok.RequiredArgsConstructor;
import mudgame.controls.events.VisibilityChange;
import mudgame.server.ServerGameState;

import java.util.Set;

@RequiredArgsConstructor
class EntityRemover {
    private final ServerGameState state;
    private final Visibility visibility;

    public RemovedEntity removeEntity(EntityID entityID) {
        PlayerID owner = state.entityBoard().entityOwner(entityID);
        state.entityBoard().removeEntity(entityID);
        ClaimChange claimChange = state.claimedArea()
                .removeEntity(entityID)
                .masked(state.terrain());
        Set<PositionVisibility> positions = state.fogOfWar()
                .playerFogOfWar(owner)
                .removeEntity(entityID);
        VisibilityChange visibilityChange = visibility.get(positions);
        return new RemovedEntity(entityID, visibilityChange, claimChange);
    }
}
