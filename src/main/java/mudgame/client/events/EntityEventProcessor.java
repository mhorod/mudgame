package mudgame.client.events;

import core.claiming.PlayerClaimedArea;
import core.entities.model.Entity;
import core.model.Position;
import lombok.extern.slf4j.Slf4j;
import mudgame.client.ClientGameState;
import mudgame.controls.events.AttackEntityEvent;
import mudgame.controls.events.KillEntity;
import mudgame.controls.events.MoveEntityAlongPath;
import mudgame.controls.events.MoveEntityAlongPath.SingleMove;
import mudgame.controls.events.PlaceEntity;
import mudgame.controls.events.RemoveEntity;
import mudgame.controls.events.SpawnEntity;
import mudgame.controls.events.VisibilityChange;
import mudgame.controls.events.VisibilityChange.HidePosition;
import mudgame.controls.events.VisibilityChange.PositionVisibilityChange;
import mudgame.controls.events.VisibilityChange.ShowPosition;

import static core.terrain.model.TerrainType.UNKNOWN;

@Slf4j
final class EntityEventProcessor {
    private final ClientGameState state;
    private final EntityManager entityManager;
    private final PlayerClaimedArea claimedArea;

    public EntityEventProcessor(ClientGameState state) {
        this.state = state;
        this.entityManager = new EntityManager(
                state.playerID(),
                state.entityBoard(),
                state.fogOfWar(),
                state.terrain()
        );
        this.claimedArea = state.claimedArea();
    }

    public void spawnEntity(SpawnEntity e) {
        entityManager.placeEntity(e.entity(), e.position());
        applyVisibilityChange(e.visibilityChange());
        claimedArea.apply(e.claimChange());
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
                .forEach(entityManager::removeEntity);
        state.terrain().setTerrainAt(h.position(), UNKNOWN);
        claimedArea.unclaim(h.position());
    }

    private void showPosition(ShowPosition s) {
        state.terrain().setTerrainAt(s.position(), s.terrain());
        s.entities().forEach(e -> entityManager.placeEntity(e, s.position()));
        if (s.positionOwner() != null)
            claimedArea.claim(s.positionOwner(), s.position());
        else
            claimedArea.unclaim(s.position());
    }

    public void moveEntityAlongPath(MoveEntityAlongPath e) {
        for (SingleMove singleMove : e.moves()) {
            Position destination = singleMove.destinationNullable();
            if (destination != null)
                entityManager.moveEntity(e.entityID(), destination);
            applyVisibilityChange(singleMove.visibilityChange());
            claimedArea.apply(singleMove.claimChange());
        }
    }

    public void placeEntity(PlaceEntity e) {
        entityManager.placeEntity(e.entity(), e.position());
    }

    public void removeEntity(RemoveEntity e) {
        entityManager.removeEntity(e.entityID());
    }

    public void attackEntity(AttackEntityEvent e) {
        entityManager.damageEntity(e.attacked(), e.damage());
    }

    public void killEntity(KillEntity e) {
        entityManager.removeEntity(e.entityID());
        applyVisibilityChange(e.visibilityChange());
        claimedArea.apply(e.claimChange());
    }
}
