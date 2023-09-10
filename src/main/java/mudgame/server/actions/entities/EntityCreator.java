package mudgame.server.actions.entities;

import core.event.EventOccurrence;
import core.fogofwar.FogOfWar;
import core.model.PlayerID;
import core.model.Position;
import core.terrain.TerrainView;
import lombok.RequiredArgsConstructor;
import mudgame.controls.actions.CreateEntity;
import mudgame.controls.events.SpawnEntity;
import mudgame.controls.events.VisibilityChange;
import mudgame.server.actions.Sender;
import mudgame.server.actions.entities.EntityManager.CreatedEntity;

@RequiredArgsConstructor
final class EntityCreator {
    private final Sender sender;
    private final EntityManager entityManager;
    private final FogOfWar fow;
    private final TerrainView terrain;
    private final Visibility visibility;

    public void createEntity(CreateEntity a) {
        CreatedEntity createdEntity = entityManager.createEntity(a.type(), a.owner(), a.position());

        fow.players()
                .stream()
                .filter(p -> fow.isVisible(a.position(), p))
                .map(p -> eventOccurrenceFor(p, a, createdEntity))
                .forEach(sender::send);
    }

    private EventOccurrence eventOccurrenceFor(
            PlayerID player, CreateEntity action, CreatedEntity createdEntity
    ) {
        if (player.equals(action.owner()))
            return EventOccurrence.of(ownerEvent(createdEntity, action.position()), player);
        else
            return EventOccurrence.of(otherEvent(player, createdEntity, action.position()), player);
    }

    private SpawnEntity ownerEvent(CreatedEntity createdEntity, Position position) {
        VisibilityChange ownerVisibilityChange = visibility.convert(
                createdEntity.changedPositions());
        return new SpawnEntity(
                createdEntity.entity(),
                position,
                ownerVisibilityChange,
                createdEntity.claimChange()
        );
    }

    private SpawnEntity otherEvent(
            PlayerID player, CreatedEntity createdEntity, Position position
    ) {
        return new SpawnEntity(
                createdEntity.entity(),
                position,
                VisibilityChange.empty(),
                createdEntity.claimChange().mask(fow.playerFogOfWar(player), terrain)
        );
    }

}
