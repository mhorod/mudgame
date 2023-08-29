package core.components;

import core.entities.EntityBoard;
import core.entities.EventEntityBoard;
import core.entities.events.CreateEntity;
import core.entities.events.MoveEntity;
import core.entities.events.PlaceEntity;
import core.entities.events.RemoveEntity;
import core.entities.model.Entity;
import core.entities.model.EntityData;
import core.events.Event;
import core.events.EventObserver;
import core.events.PlayerEventObserver;
import core.fogofwar.FogOfWar;
import core.model.EntityID;
import core.model.PlayerID;
import core.model.Position;
import core.server.ServerVisibilityPredicates;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class EventEntityBoardTest {
    final EntityData MOCK_DATA = mock(EntityData.class);

    final PlayerID PLAYER_0 = new PlayerID(0);
    final PlayerID PLAYER_1 = new PlayerID(1);
    final PlayerID PLAYER_2 = new PlayerID(2);

    final EntityID ENTITY_0 = new EntityID(0);
    final Position POSITION_0 = new Position(0, 0);
    final Position POSITION_1 = new Position(0, 1);

    List<PlayerID> players;
    EntityBoard entityBoard;
    FogOfWar fow;
    ConditionalEventSender eventSender;
    EventEntityBoard testee;

    @BeforeEach
    void init() {
        players = IntStream.range(0, 3).mapToObj(PlayerID::new).toList();
        entityBoard = new EntityBoard();
        fow = mock(FogOfWar.class);
        when(fow.isVisible(any(), any())).thenReturn(false);
        eventSender = new ConditionalEventSender();
        testee = new EventEntityBoard(
                entityBoard,
                new ServerVisibilityPredicates(fow),
                eventSender
        );
    }

    @Nested
    class EventApplicationTest {
        @Test
        void creates_entity_on_board() {
            // given
            Event event = new CreateEntity(MOCK_DATA, PLAYER_0, POSITION_0);

            // when
            testee.receive(event);

            // then
            assertThat(entityBoard.allEntities()).hasSize(1);
            assertThat(entityBoard.entitiesAt(POSITION_0)).hasSize(1);
        }

        @Test
        void places_entity_on_board() {
            // given
            Entity entity = new Entity(MOCK_DATA, ENTITY_0, PLAYER_0);
            Position position = POSITION_0;
            Event event = new PlaceEntity(entity, position);

            // when
            testee.receive(event);

            // then
            assertThat(entityBoard.allEntities()).containsExactly(entity);
            assertThat(entityBoard.entitiesAt(position)).containsExactly(entity);
        }

        @Test
        void moves_entity_on_board() {
            // given
            Position from = POSITION_0;
            Position to = POSITION_1;

            Entity entity = new Entity(MOCK_DATA, ENTITY_0, PLAYER_0);
            entityBoard.placeEntity(entity, from);
            Event event = new MoveEntity(entity.id(), to);

            // when
            testee.receive(event);

            // then
            assertThat(entityBoard.entitiesAt(from)).isEmpty();
            assertThat(entityBoard.entitiesAt(to)).containsExactly(entity);

        }

        @Test
        void removes_entity_from_board() {
            // given
            Entity entity = new Entity(MOCK_DATA, ENTITY_0, PLAYER_0);
            entityBoard.placeEntity(entity, POSITION_0);

            Event event = new RemoveEntity(entity.id());

            // when
            testee.receive(event);

            // then
            assertThat(entityBoard.allEntities()).isEmpty();
        }
    }

    @Nested
    class EventPropagationTest {
        @Test
        void creating_entity_produces_place_event() {
            // given
            Position position = POSITION_0;
            Event event = new CreateEntity(MOCK_DATA, PLAYER_0, position);
            EventObserver observer = mock(EventObserver.class);
            eventSender.addObserver(observer);

            // when
            testee.receive(event);

            // then
            Entity entity = entityBoard.allEntities().get(0);
            Event expectedEvent = new PlaceEntity(entity, position);
            verify(observer).receive(expectedEvent);
        }

        @Test
        void placing_entity_produces_place_event() {
            // given
            EventObserver observer = mock(EventObserver.class);
            eventSender.addObserver(observer);

            Entity entity = new Entity(MOCK_DATA, ENTITY_0, PLAYER_0);
            Event event = new PlaceEntity(entity, POSITION_0);

            // when
            testee.receive(event);

            // then
            verify(observer).receive(event);
        }

        @Test
        void removing_entity_produces_remove_event() {
            // given
            EventObserver observer = mock(EventObserver.class);
            eventSender.addObserver(observer);

            Entity entity = new Entity(MOCK_DATA, ENTITY_0, PLAYER_0);
            entityBoard.placeEntity(entity, POSITION_0);

            Event event = new RemoveEntity(entity.id());

            // when
            testee.receive(event);

            // then
            verify(observer).receive(event);
        }

        @Test
        void moving_entity_produces_remove_event() {
            // given
            EventObserver observer = mock(EventObserver.class);
            eventSender.addObserver(observer);

            Entity entity = new Entity(MOCK_DATA, ENTITY_0, PLAYER_0);
            entityBoard.placeEntity(entity, POSITION_0);

            Event event = new MoveEntity(entity.id(), POSITION_1);

            // when
            testee.receive(event);

            // then
            verify(observer).receive(event);
        }
    }

    @Nested
    class FogOfWarTest {
        @Test
        void player_receives_create_event_effect_when_sees_position() {
            // given
            Position position = POSITION_0;

            EventObserver observerWithView = mock(EventObserver.class);
            EventObserver observerWithoutView = mock(EventObserver.class);

            sees(PLAYER_0, position);
            eventSender.addObserver(new PlayerEventObserver(PLAYER_0, observerWithView));
            eventSender.addObserver(new PlayerEventObserver(PLAYER_1, observerWithoutView));

            Event event = new CreateEntity(MOCK_DATA, PLAYER_0, position);

            // when
            testee.receive(event);

            // then
            Entity entity = entityBoard.allEntities().get(0);
            Event expectedEvent = new PlaceEntity(entity, position);
            verify(observerWithView).receive(expectedEvent);
            verifyNoInteractions(observerWithoutView);
        }

        @Test
        void player_receives_place_event_when_sees_position() {
            // given
            Position position = POSITION_0;

            EventObserver observerWithView = mock(EventObserver.class);
            EventObserver observerWithoutView = mock(EventObserver.class);

            sees(PLAYER_0, position);
            eventSender.addObserver(new PlayerEventObserver(PLAYER_0, observerWithView));
            eventSender.addObserver(new PlayerEventObserver(PLAYER_1, observerWithoutView));
            Entity entity = new Entity(MOCK_DATA, ENTITY_0, PLAYER_0);
            Event event = new PlaceEntity(entity, POSITION_0);

            // when
            testee.receive(event);

            // then
            verify(observerWithView).receive(event);
            verifyNoInteractions(observerWithoutView);
        }

        @Test
        void player_receives_remove_event_when_sees_entity_position() {
            // given
            Position position = POSITION_0;

            EventObserver observerWithView = mock(EventObserver.class);
            EventObserver observerWithoutView = mock(EventObserver.class);

            sees(PLAYER_0, position);
            eventSender.addObserver(new PlayerEventObserver(PLAYER_0, observerWithView));
            eventSender.addObserver(new PlayerEventObserver(PLAYER_1, observerWithoutView));

            Entity entity = new Entity(MOCK_DATA, ENTITY_0, PLAYER_0);
            entityBoard.placeEntity(entity, POSITION_0);

            Event event = new RemoveEntity(entity.id());

            // when
            testee.receive(event);

            // then
            verify(observerWithView).receive(event);
            verifyNoInteractions(observerWithoutView);
        }

        @Test
        void player_receives_move_event_when_sees_either_end() {
            // given
            Position from = POSITION_0;
            Position to = POSITION_1;

            EventObserver observerWithFromView = mock(EventObserver.class);
            EventObserver observerWithToView = mock(EventObserver.class);
            EventObserver observerWithoutView = mock(EventObserver.class);

            sees(PLAYER_0, from);
            sees(PLAYER_1, to);

            eventSender.addObserver(new PlayerEventObserver(PLAYER_0, observerWithFromView));
            eventSender.addObserver(new PlayerEventObserver(PLAYER_1, observerWithToView));
            eventSender.addObserver(new PlayerEventObserver(PLAYER_2, observerWithoutView));

            Entity entity = new Entity(MOCK_DATA, ENTITY_0, PLAYER_0);
            entityBoard.placeEntity(entity, from);

            Event event = new MoveEntity(entity.id(), to);

            // when
            testee.receive(event);

            // then
            verify(observerWithFromView).receive(event);
            verify(observerWithToView).receive(event);
            verifyNoInteractions(observerWithoutView);
        }

    }

    void sees(PlayerID playerID, Position... positions) {
        for (Position position : positions) {
            when(fow.isVisible(position, playerID)).thenReturn(true);
        }
    }

}