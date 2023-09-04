package mudgame.server.actions;

import core.entities.model.Entity;
import core.fogofwar.PlayerFogOfWar;
import core.fogofwar.events.VisibilityChange;
import core.model.EntityID;
import core.model.Position;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Set;

@Slf4j
@RequiredArgsConstructor
final class EventPlayerFogOfWar {
    private final PlayerFogOfWar playerFow;

    VisibilityChange placeEntity(Entity entity, Position position) {
        return process(playerFow.placeEntity(entity, position));
    }


    VisibilityChange moveEntity(EntityID entityID, Position destination) {
        return process(playerFow.moveEntity(entityID, destination));
    }

    VisibilityChange removeEntity(EntityID entityID) {
        return process(playerFow.removeEntity(entityID));
    }

    private VisibilityChange process(Set<Position> changedPositions) {
        return VisibilityChange.empty();
    }
}
