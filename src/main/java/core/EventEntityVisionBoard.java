package core;

import core.entities.EntityVisionBoard;
import core.entities.events.MoveEntity;
import core.entities.events.PlaceEntity;
import core.entities.events.RemoveEntity;
import core.events.model.Event;
import core.events.observers.EventObserver;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class EventEntityVisionBoard implements EventObserver {

    private final EntityVisionBoard entityBoard;

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
        entityBoard.placeEntity(e.entity(), e.position());
    }

    private void moveEntity(MoveEntity e) {
        entityBoard.moveEntity(e.entityID(), e.destination());
    }

    private void removeEntity(RemoveEntity e) {
        entityBoard.removeEntity(e.entityID());
    }


}
