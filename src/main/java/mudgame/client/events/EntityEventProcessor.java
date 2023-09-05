package mudgame.client.events;

import core.entities.model.Entity;
import mudgame.client.ClientGameState;
import mudgame.controls.events.MoveEntityAlongPath;
import mudgame.controls.events.MoveEntityAlongPath.SingleMove;
import mudgame.controls.events.SpawnEntity;
import mudgame.controls.events.VisibilityChange;
import mudgame.controls.events.VisibilityChange.HidePosition;
import mudgame.controls.events.VisibilityChange.PositionVisibilityChange;
import mudgame.controls.events.VisibilityChange.ShowPosition;

import static core.terrain.model.TerrainType.UNKNOWN;

final class EntityEventProcessor {
    private final ClientGameState state;

    public EntityEventProcessor(ClientGameState state) {
        this.state = state;
    }

    public void spawnEntity(SpawnEntity e) {
        state.entityBoard().placeEntity(e.entity(), e.position());
        applyVisibilityChange(e.visibilityChange());
    }

    private void applyVisibilityChange(VisibilityChange visibilityChange) {
        for (PositionVisibilityChange p : visibilityChange.positions())
            if (p instanceof ShowPosition s)
                showPosition(s);
            else if (p instanceof HidePosition h)
                hidePosition(h);
    }

    private void hidePosition(HidePosition h) {
        state.entityBoard()
                .entitiesAt(h.position())
                .stream()
                .map(Entity::id)
                .forEach(id -> state.entityBoard().removeEntity(id));
        state.terrain().setTerrainAt(h.position(), UNKNOWN);
    }

    private void showPosition(ShowPosition s) {
        state.terrain().setTerrainAt(s.position(), s.terrain());
        s.entities().forEach(e -> state.entityBoard().placeEntity(e, s.position()));
    }

    public void moveEntityAlongPath(MoveEntityAlongPath e) {
        for (SingleMove singleMove : e.moves()) {
            if (singleMove.destination().isPresent()) {
                state.entityBoard().moveEntity(e.entityID(), singleMove.destination().get());
                state.fogOfWar().moveEntity(e.entityID(), singleMove.destination().get());
            }
            applyVisibilityChange(singleMove.visibilityChange());
        }
    }
}
