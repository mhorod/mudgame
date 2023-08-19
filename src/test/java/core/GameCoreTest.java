package core;


import core.entities.events.PlaceEntity;
import core.entities.model.Entity;
import core.entities.model.EntityData;
import core.events.Event;
import core.events.Event.Action;
import core.events.EventObserver;
import core.events.EventSender;
import core.events.ObserverEventSender;
import core.events.PlayerEventObserver;
import core.model.EntityID;
import core.model.PlayerID;
import core.model.Position;
import core.turns.CompleteTurn;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.List;

import static core.EntityActions.create;
import static core.EntityBoardAssert.assertThat;
import static core.EntityEvents.create;
import static core.EntityEvents.move;
import static core.EntityEvents.place;
import static core.EntityEvents.remove;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class GameCoreTest
{
    static final Position POSITION_0_0 = new Position(0, 0);
    static final Position POSITION_0_1 = new Position(0, 1);
    static final EventSender MOCK_SENDER = mock(EventSender.class);


    /**
     * Test that game core ends up in correct state when receiving events
     */
    @Nested
    class EventApplicationTest
    {
        @Nested
        class EntityEventsTest
        {
            GameCore core;

            @BeforeEach
            void init()
            {
                core = new GameCore(2, MOCK_SENDER);
            }

            @Test
            void test_create_entity()
            {
                // given
                EntityData data = mock(EntityData.class);
                Position position = POSITION_0_0;

                // when
                send(core, create(data, 0, position));

                // then
                assertThat(core.entityBoard.allEntities()).hasSize(1);
                assertThat(core.entityBoard.entitiesAt(position)).hasSize(1);
            }

            @Test
            void test_place_entity()
            {
                // given
                Entity entity = mockEntity(0, 0);
                Position position = POSITION_0_0;

                // when
                send(core, place(entity, position));

                // then
                assertThat(core.entityBoard)
                        .containsExactlyEntities(entity)
                        .containsEntityWithId(entity.id());

                assertThat(core.entityBoard.entitiesAt(position))
                        .containsExactly(entity);
            }

            @Test
            void test_remove_entity()
            {
                // given
                Entity entity = mockEntity(0, 0);
                Position position = POSITION_0_0;

                // when
                send(core,
                     place(entity, position),
                     remove(entity.id())
                );

                // then
                assertThat(core.entityBoard)
                        .containsNoEntities()
                        .doesNotContainEntityWithId(entity.id());

                assertThat(core.entityBoard.entitiesAt(position)).isEmpty();
            }

            @Test
            void test_move_entity()
            {
                // given
                Entity entity = mockEntity(0, 0);
                Position source = POSITION_0_0;
                Position destination = POSITION_0_1;

                // when
                send(core,
                     place(entity, source),
                     move(entity.id(), destination)
                );

                // then
                assertThat(core.entityBoard)
                        .containsExactlyEntities(entity)
                        .containsEntityWithId(entity.id());

                assertThat(core.entityBoard.entitiesAt(source)).isEmpty();
                assertThat(core.entityBoard.entitiesAt(destination)).containsExactly(entity);
            }
        }

        @Nested
        class TurnEventsTest
        {
            GameCore core;

            @BeforeEach
            void init()
            {
                core = new GameCore(2, MOCK_SENDER);
            }

            @Test
            void player_changes_after_completing_turn()
            {
                // given
                List<PlayerID> players = core.playerManager.getPlayerIDs();

                // when
                send(core, completeTurn());

                // then
                assertThat(core.playerManager.getCurrentPlayer()).isEqualTo(players.get(1));
            }

            @Test
            void first_player_has_turn_again_when_all_players_complete_turn()
            {
                // given
                List<PlayerID> players = core.playerManager.getPlayerIDs();

                // when
                send(core, completeTurn(), completeTurn());

                // then
                assertThat(core.playerManager.getCurrentPlayer()).isEqualTo(players.get(0));
            }
        }
    }

    /**
     * Test that game core ends up in correct state and sends appropriate events.
     * when receiving actions
     */
    @Nested
    class ActionProcessingTest
    {
        GameCore core;
        ObserverEventSender eventSender;
        List<PlayerEventObserver> playerEventObservers;
        EventObserver eventObserver;

        @BeforeEach
        void init()
        {
            eventSender = new ObserverEventSender();
            core = new GameCore(3, eventSender);
            List<PlayerID> players = core.playerManager.getPlayerIDs();
            playerEventObservers = players.stream()
                    .map(player -> new PlayerEventObserver(player, mock(EventObserver.class)))
                    .toList();
            playerEventObservers.forEach(o -> eventSender.addObserver(o));

            eventObserver = mock(EventObserver.class);
            eventSender.addObserver(eventObserver);
        }

        void verifyNoEvents()
        {
            for (PlayerEventObserver o : playerEventObservers)
                verifyNoInteractions(o.observer());
            verifyNoInteractions(eventObserver);
        }

        @Nested
        class EntityActionsTest
        {
            @Test
            void entity_is_not_created_when_player_does_not_see_position()
            {
                // when
                process(core, create(0, mock(EntityData.class), 0, POSITION_0_0));

                // then
                assertThat(core.entityBoard).containsNoEntities();
                verifyNoEvents();
            }

            @Test
            void entity_is_not_created_when_player_does_not_own_it()
            {
                // given
                core.fogOfWar.setVisibility(POSITION_0_0, new PlayerID(0), true);

                // when
                process(core, create(0, mock(EntityData.class), 1, POSITION_0_0));

                // then
                assertThat(core.entityBoard).containsNoEntities();
                verifyNoEvents();
            }

            @Test
            void entity_is_not_created_when_position_is_occupied()
            {
                // given
                core.fogOfWar.setVisibility(POSITION_0_0, new PlayerID(0), true);
                core.entityBoard.placeEntity(mock(Entity.class), POSITION_0_0);

                // when
                process(core, create(0, mock(EntityData.class), 0, POSITION_0_0));

                // then
                assertThat(core.entityBoard.allEntities()).hasSize(1);
                verifyNoEvents();
            }

            @Test
            void entity_is_created_when_all_conditions_are_met()
            {
                // given
                core.fogOfWar.setVisibility(POSITION_0_0, new PlayerID(0), true);

                // when
                process(core, create(0, mock(EntityData.class), 0, POSITION_0_0));

                // then
                assertThat(core.entityBoard.allEntities()).hasSize(1);

                ArgumentCaptor<Event> captor = ArgumentCaptor.forClass(Event.class);
                verify(eventObserver).receive(captor.capture());
                assertThat(captor.getValue()).isInstanceOf(PlaceEntity.class);
            }
        }

        @Nested
        class TurnActionsTest
        {
            @Test
            void player_changes_when_current_player_completes_turn()
            {
                // given
                PlayerID playerBefore = core.playerManager.getCurrentPlayer();

                // when
                process(core, completeTurn(0));

                // then
                PlayerID playerAfter = core.playerManager.getCurrentPlayer();
                assertThat(playerBefore).isNotEqualTo(playerAfter);

                playerEventObservers.forEach(o -> verify(o.observer()).receive(completeTurn()));
                verify(eventObserver).receive(completeTurn());
            }

            @Test
            void player_does_not_change_when_not_current_player_completes_turn()
            {
                // given
                PlayerID playerBefore = core.playerManager.getCurrentPlayer();

                // when
                process(core, completeTurn(1));

                // then
                PlayerID playerAfter = core.playerManager.getCurrentPlayer();
                assertThat(playerBefore).isEqualTo(playerAfter);
                verifyNoEvents();
            }

        }
    }

    static Entity mockEntity(long entityID, long playerID)
    {
        return new Entity(mock(EntityData.class), new EntityID(entityID), new PlayerID(playerID));
    }


    static void send(GameCore core, Event... events)
    {
        for (Event event : events)
            core.receive(event);
    }

    @SafeVarargs
    static void process(GameCore core, PlayerAction<? extends Action>... actions)
    {
        for (PlayerAction<? extends Action> action : actions)
            core.process(action.action(), action.actor());
    }

    static CompleteTurn completeTurn() { return new CompleteTurn(); }

    static PlayerAction<CompleteTurn> completeTurn(long actor)
    {
        return PlayerAction.from(actor, completeTurn());
    }

}