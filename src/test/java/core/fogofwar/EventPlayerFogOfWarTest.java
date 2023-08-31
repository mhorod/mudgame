package core.fogofwar;

import core.entities.components.Vision;
import core.entities.events.MoveEntity;
import core.entities.events.PlaceEntity;
import core.entities.events.RemoveEntity;
import core.entities.model.Entity;
import core.events.EventObserver;
import core.fogofwar.events.SetVisibility;
import core.model.EntityID;
import core.model.PlayerID;
import core.model.Position;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.mockito.Mockito.*;

class EventPlayerFogOfWarTest {
    EventObserver observer;
    PlayerFogOfWar fow;
    EventPlayerFogOfWar testee;

    @BeforeEach
    void init() {
        observer = mock(EventObserver.class);
        fow = new PlayerFogOfWar();
        testee = new EventPlayerFogOfWar(fow, observer);
    }

    @Test
    void placing_entity_without_vision_does_not_generate_event() {
        // given
        Entity entity = new Entity(List.of(), mock(EntityID.class), mock(PlayerID.class));
        PlaceEntity event = new PlaceEntity(entity, new Position(0, 0));

        // when
        testee.receive(event);

        // then
        verifyNoInteractions(observer);
    }

    @Test
    void moving_entity_without_vision_does_not_generate_event() {
        // given
        Entity entity = new Entity(List.of(), mock(EntityID.class), mock(PlayerID.class));
        PlaceEntity placeEvent = new PlaceEntity(entity, new Position(0, 0));
        MoveEntity moveEvent = new MoveEntity(entity.id(), new Position(0, 1));

        // when
        testee.receive(placeEvent);
        testee.receive(moveEvent);

        // then
        verifyNoInteractions(observer);
    }

    @Test
    void removing_entity_without_vision_does_not_generate_event() {
        // given
        Entity entity = new Entity(List.of(), mock(EntityID.class), mock(PlayerID.class));
        PlaceEntity placeEvent = new PlaceEntity(entity, new Position(0, 0));
        RemoveEntity removeEvent = new RemoveEntity(entity.id());

        // when
        testee.receive(placeEvent);
        testee.receive(removeEvent);

        // then
        verifyNoInteractions(observer);
    }

    @Test
    void placing_entity_with_vision_generates_event() {
        // given
        Entity entity = entityWithVision(1);
        PlaceEntity event = new PlaceEntity(entity, new Position(0, 0));

        // when
        testee.receive(event);

        // then
        verify(observer).receive(any(SetVisibility.class));
        verify(observer).receive(argThat(e -> ((SetVisibility) e).postions().size() == 9));
    }

    @Test
    void moving_entity_with_vision_generates_event_with_changed_positions() {
        // given
        Entity entity = entityWithVision(1);
        fow.placeEntity(entity, new Position(0, 0));

        MoveEntity event = new MoveEntity(entity.id(), new Position(0, 1));
        // when
        testee.receive(event);

        // then
        verify(observer).receive(any(SetVisibility.class));
        // 3 removed and 3 added
        verify(observer).receive(argThat(e -> ((SetVisibility) e).postions().size() == 6));
    }

    static Entity entityWithVision(int range) {
        return new Entity(
                List.of(new Vision(range)),
                mock(EntityID.class),
                mock(PlayerID.class)
        );
    }
}