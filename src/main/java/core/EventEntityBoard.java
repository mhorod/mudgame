package core;

import core.entities.Entity;
import core.entities.EntityBoard;
import core.events.Event;
import core.events.Event.CreateEntity;
import core.events.Event.MoveEntity;
import core.events.Event.PlaceEntity;
import core.events.Event.RemoveEntity;
import core.events.EventObserver;
import core.events.EventSourceBase;
import core.id.PlayerID;
import lombok.RequiredArgsConstructor;

import java.util.function.Predicate;


@RequiredArgsConstructor
public class EventEntityBoard extends EventSourceBase implements EventObserver
{
    private final EntityBoard board;
    private final FogOfWarView fow;

    @Override
    public void receive(Event event)
    {
        if (event instanceof CreateEntity e)
            createEntity(e);
        else if (event instanceof PlaceEntity e)
            placeEntity(e);
        else if (event instanceof MoveEntity e)
            moveEntity(e);
        else if (event instanceof RemoveEntity e)
            removeEntity(e);
    }

    private void removeEntity(RemoveEntity event)
    {
        if (!board.containsEntity(event.entityID()))
            return;

        Position position = board.entityPosition(event.entityID());
        board.removeEntity(event.entityID());
        send(event, isVisible(position));
    }

    private void moveEntity(MoveEntity event)
    {
        if (!board.containsEntity(event.entityID()))
            return;

        Position from = board.entityPosition(event.entityID());
        Position to = event.position();
        board.moveEntity(event.entityID(), to);
        send(event, isMoveVisible(from, to));
    }

    private void placeEntity(PlaceEntity event)
    {
        if (board.containsEntity(event.entity().id()))
            return;

        board.placeEntity(event.entity(), event.position());
        send(event, isVisible(event.position()));
    }

    private void createEntity(CreateEntity event)
    {
        Entity entity = board.createEntity(event.entityData(), event.owner(), event.position());
        PlaceEntity resultEvent = new Event.PlaceEntity(entity, event.position());
        send(resultEvent, isVisible(event.position()));
    }

    private Predicate<PlayerID> isVisible(Position position)
    {
        return id -> fow.isVisible(position, id);
    }

    private Predicate<PlayerID> isMoveVisible(Position from, Position to)
    {
        return id -> fow.isVisible(from, id) || fow.isVisible(to, id);
    }
}
