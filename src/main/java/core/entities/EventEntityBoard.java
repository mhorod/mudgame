package core.entities;

import core.entities.events.CreateEntity;
import core.entities.events.MoveEntity;
import core.entities.events.PlaceEntity;
import core.entities.events.RemoveEntity;
import core.entities.model.Entity;
import core.events.ConditionalEventObserver;
import core.events.Event;
import core.events.EventObserver;
import core.events.EventOccurrence;
import core.events.EventOccurrenceObserver;
import core.fogofwar.events.SetVisibility;
import core.fogofwar.events.SetVisibility.SetPositionVisibility;
import core.model.PlayerID;
import core.model.Position;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.function.Predicate;


@Slf4j
@RequiredArgsConstructor
public final class EventEntityBoard implements EventObserver, EventOccurrenceObserver {


    public interface VisibilityPredicates {
        Predicate<PlayerID> isVisible(Position position);

        Predicate<PlayerID> isMoveVisible(Position from, Position to);
    }


    private final EntityBoard board;
    private final VisibilityPredicates visibilityPredicates;
    private final ConditionalEventObserver conditionalEventObserver;

    @Override
    public void receive(Event event) {
        log.info("Received event: {}", event);
        if (event instanceof CreateEntity e)
            createEntity(e);
        else if (event instanceof PlaceEntity e)
            placeEntity(e);
        else if (event instanceof MoveEntity e)
            moveEntity(e);
        else if (event instanceof RemoveEntity e)
            removeEntity(e);
    }

    @Override
    public void receive(EventOccurrence eventOccurrence) {
        if (eventOccurrence.event() instanceof SetVisibility e)
            handleSetVisibility(e.positions(), eventOccurrence.recipients());
    }

    private void handleSetVisibility(
            List<SetPositionVisibility> positions, List<PlayerID> recipients
    ) {
        for (SetPositionVisibility positionVisibility : positions)
            for (PlayerID player : recipients)
                changeOnePosition(positionVisibility, player);
    }

    private void changeOnePosition(SetPositionVisibility positionVisibility, PlayerID player) {
        for (Entity e : board.entitiesAt(positionVisibility.position())) {
            Event event;
            if (positionVisibility.isVisible()) {
                event = new PlaceEntity(e, positionVisibility.position());
            } else {
                event = new RemoveEntity(e.id());
            }
            conditionalEventObserver.receive(event, player::equals);
        }
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
        Entity entity = board.createEntity(event.components(), event.owner(), event.position());
        PlaceEntity resultEvent = new PlaceEntity(entity, event.position());
        conditionalEventObserver.receive(resultEvent, isVisible(event.position()));
    }

    private Predicate<PlayerID> isVisible(Position position) {
        return visibilityPredicates.isVisible(position);
    }

    private Predicate<PlayerID> isMoveVisible(Position from, Position to) {
        return visibilityPredicates.isMoveVisible(from, to);
    }
}
