package mudgame.server.internal;

import core.claiming.ClaimedAreaView.ClaimChange;
import core.entities.model.Entity;
import core.fogofwar.PlayerFogOfWar.PositionVisibility;
import core.model.EntityID;
import core.model.Position;
import lombok.RequiredArgsConstructor;
import mudgame.controls.events.VisibilityChange;
import mudgame.server.ServerGameState;

import java.util.Set;

@RequiredArgsConstructor
public class EntityMover {
    private final ServerGameState state;
    private final Visibility visibility;

    public record MovedEntity(
            EntityID entityID,
            VisibilityChange visibilityChange,
            ClaimChange claimChange
    ) { }

    public MovedEntity moveEntity(EntityID entityID, Position position) {
        Entity entity = state.entityBoard().findEntityByID(entityID);
        state.entityBoard().moveEntity(entityID, position);
        ClaimChange claimChange = state.claimedArea()
                .moveEntity(entity, position)
                .masked(state.terrain());
        Set<PositionVisibility> positions = state.fogOfWar()
                .playerFogOfWar(entity.owner())
                .moveEntity(entityID, position);
        VisibilityChange visibilityChange = visibility.get(positions);
        return new MovedEntity(entityID, visibilityChange, claimChange);
    }
}
