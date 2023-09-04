package mudgame.server.actions;

import core.entities.model.Entity;
import core.fogofwar.FogOfWar;
import core.fogofwar.events.VisibilityChange;
import core.model.EntityID;
import core.model.PlayerID;
import core.model.Position;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

import static java.util.stream.Collectors.toMap;

@Slf4j
final class EventFogOfWar {
    private final FogOfWar fow;
    private final Map<PlayerID, EventPlayerFogOfWar> eventFows;


    EventFogOfWar(FogOfWar fow) {
        this.fow = fow;
        eventFows = fow.players()
                .stream()
                .collect(toMap(p -> p, this::mapToPlayerFow));
    }

    private EventPlayerFogOfWar mapToPlayerFow(PlayerID id) {
        return new EventPlayerFogOfWar(fow.playerFogOfWar(id));
    }

    public VisibilityChange placeEntity(PlayerID player, Entity entity, Position position) {
        return eventFows.get(player).placeEntity(entity, position);
    }

    public VisibilityChange moveEntity(PlayerID player, EntityID entityID, Position destination) {
        return eventFows.get(player).moveEntity(entityID, destination);
    }
}
