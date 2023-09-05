package mudgame.server.actions;

import core.entities.model.Entity;
import core.fogofwar.FogOfWar;
import core.fogofwar.PlayerFogOfWar;
import core.fogofwar.PlayerFogOfWar.PositionVisibility;
import core.model.EntityID;
import core.model.PlayerID;
import core.model.Position;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.Set;

import static java.util.stream.Collectors.toMap;

@Slf4j
final class EventFogOfWar {
    private final FogOfWar fow;
    private final Map<PlayerID, PlayerFogOfWar> eventFows;


    EventFogOfWar(FogOfWar fow) {
        this.fow = fow;
        eventFows = fow.players()
                .stream()
                .collect(toMap(p -> p, fow::playerFogOfWar));
    }


    public Set<PositionVisibility> placeEntity(PlayerID player, Entity entity, Position position) {
        return eventFows.get(player).placeEntity(entity, position);
    }

    public Set<PositionVisibility> moveEntity(
            PlayerID player, EntityID entityID, Position destination
    ) {
        return eventFows.get(player).moveEntity(entityID, destination);
    }
}
