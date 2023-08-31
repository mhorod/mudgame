package core.fogofwar;

import core.entities.events.MoveEntity;
import core.entities.events.PlaceEntity;
import core.entities.events.RemoveEntity;
import core.events.Event;
import core.events.EventObserver;
import core.fogofwar.events.SetVisibility;
import core.fogofwar.events.SetVisibility.SetPositionVisibility;
import core.model.Position;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Set;

@RequiredArgsConstructor
public class EventPlayerFogOfWar implements EventObserver {

    private final PlayerFogOfWar playerFow;
    private final EventObserver eventObserver;

    @Override
    public void receive(Event event) {
        if (event instanceof PlaceEntity e)
            placeEntity(e);
        else if (event instanceof MoveEntity e)
            moveEntity(e);
        else if (event instanceof RemoveEntity e)
            removeEntity(e);
    }

    private void placeEntity(PlaceEntity e) {
        process(playerFow.placeEntity(e.entity(), e.position()));
    }


    private void moveEntity(MoveEntity e) {
        process(playerFow.moveEntity(e.entityID(), e.destination()));
    }

    private void removeEntity(RemoveEntity e) {
        process(playerFow.removeEntity(e.entityID()));
    }

    private void process(Set<Position> changedPositions) {
        List<SetPositionVisibility> changes = changedPositions
                .stream()
                .map(pos -> new SetPositionVisibility(pos, playerFow.isVisible(pos)))
                .toList();
        if (!changes.isEmpty())
            eventObserver.receive(new SetVisibility(changes));
    }
}
