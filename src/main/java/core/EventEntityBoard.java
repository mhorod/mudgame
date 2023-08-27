package core;

import core.entities.EntityBoard;
import core.entities.events.CreateEntity;
import core.entities.events.MoveEntity;
import core.entities.events.PlaceEntity;
import core.entities.events.RemoveEntity;
import core.entities.model.Entity;
import core.events.model.Event;
import core.events.observers.ConditionalEventObserver;
import core.events.observers.EventObserver;
import core.fogofwar.FogOfWarView;
import core.model.PlayerID;
import core.model.Position;
import lombok.RequiredArgsConstructor;

import java.util.function.Predicate;


@RequiredArgsConstructor
public final class EventEntityBoard implements EventObserver {
    private final EntityBoard board;
    private final FogOfWarView fow;
    private final ConditionalEventObserver conditionalEventObserver;

    @Override
    public void receive(Event event) {
        if (event instanceof CreateEntity e)
            createEntity(e);
        else if (event instanceof PlaceEntity e)
            placeEntity(e);
        else if (event instanceof MoveEntity e)
            moveEntity(e);
        else if (event instanceof RemoveEntity e)
            removeEntity(e);
    }

    private void removeEntity(RemoveEntity event) {
        if (!board.containsEntity(event.entityID()))
            return;

        Position position = board.entityPosition(event.entityID());
        board.removeEntity(event.entityID());
        conditionalEventObserver.receive(event, isVisible(position));
    }

    private void moveEntity(MoveEntity event) {
        if (!board.containsEntity(event.entityID()))
            return;

        Position from = board.entityPosition(event.entityID());
        Position to = event.destination();
        board.moveEntity(event.entityID(), to);
        conditionalEventObserver.receive(event, isMoveVisible(from, to));
    }

    private void placeEntity(PlaceEntity event) {
        if (board.containsEntity(event.entity().id()))
            return;

        board.placeEntity(event.entity(), event.position());
        conditionalEventObserver.receive(event, isVisible(event.position()));
    }

    private void createEntity(CreateEntity event) {
        Entity entity = board.createEntity(event.entityData(), event.owner(), event.position());
        PlaceEntity resultEvent = new PlaceEntity(entity, event.position());
        conditionalEventObserver.receive(resultEvent, isVisible(event.position()));
    }

    private Predicate<PlayerID> isVisible(Position position) {
        return id -> fow.isVisible(position, id);
    }

    private Predicate<PlayerID> isMoveVisible(Position from, Position to) {
        return id -> fow.isVisible(from, id) || fow.isVisible(to, id);
    }
}
