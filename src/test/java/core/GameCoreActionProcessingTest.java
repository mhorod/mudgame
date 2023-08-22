package core;

import core.entities.events.MoveEntity;
import core.entities.events.PlaceEntity;
import core.entities.model.Entity;
import core.entities.model.EntityData;
import core.events.EventObserver;
import core.events.ObserverEventSender;
import core.events.PlayerEventObserver;
import core.model.EntityID;
import core.model.PlayerID;
import core.model.Position;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;

import static core.EntityActions.create;
import static core.EntityActions.move;
import static core.EventTestUtils.process;
import static core.TurnEvents.completeTurn;
import static core.entities.EntityBoardAssert.assertThat;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

/**
 * Test that game core ends up in correct state and sends appropriate events.
 * when receiving actions
 */
class GameCoreActionProcessingTest {
    static final Position POSITION_0_0 = new Position(0, 0);
    static final Position POSITION_0_1 = new Position(0, 1);

    GameCore core;
    ObserverEventSender eventSender;
    List<PlayerEventObserver> playerEventObservers;
    EventObserver eventObserver;

    @BeforeEach
    void init() {
        eventSender = new ObserverEventSender();
        core = new GameCore(4, eventSender);
        List<PlayerID> players = core.playerManager.getPlayerIDs();
        playerEventObservers = players.stream()
                .map(player -> new PlayerEventObserver(player, mock(EventObserver.class)))
                .toList();
        playerEventObservers.forEach(o -> eventSender.addObserver(o));

        eventObserver = mock(EventObserver.class);
        eventSender.addObserver(eventObserver);
    }

    void verifyNoEvents() {
        for (PlayerEventObserver o : playerEventObservers)
            verifyNoInteractions(o.observer());
        verifyNoInteractions(eventObserver);
    }

    @Nested
    class EntityActionsTest {
        @Nested
        class CreateEntityTest {
            @Test
            void entity_is_not_created_when_player_does_not_see_position() {
                // when
                process(core, create(0, mock(EntityData.class), 0, POSITION_0_0));

                // then
                assertThat(core.entityBoard).containsNoEntities();
                verifyNoEvents();
            }

            @Test
            void entity_is_not_created_when_player_does_not_own_it() {
                // given
                sees(core, 0, POSITION_0_0);

                // when
                process(core, create(0, mock(EntityData.class), 1, POSITION_0_0));

                // then
                assertThat(core.entityBoard).containsNoEntities();
                verifyNoEvents();
            }

            @Test
            void entity_is_not_created_when_position_is_occupied() {
                // given
                sees(core, 0, POSITION_0_0);
                Entity entity = mock(Entity.class);
                core.entityBoard.placeEntity(entity, POSITION_0_0);

                // when
                process(core, create(0, mock(EntityData.class), 0, POSITION_0_0));

                // then
                assertThat(core.entityBoard).containsExactlyEntities(entity);
                verifyNoEvents();
            }

            @Test
            void entity_is_not_created_when_other_player_has_turn() {
                // given
                sees(core, 0, POSITION_0_0);

                // when
                process(core, create(1, mock(EntityData.class), 1, POSITION_0_0));

                // then
                assertThat(core.entityBoard).containsNoEntities();
                verifyNoEvents();
            }

            @Test
            void entity_is_created_when_all_conditions_are_met() {
                // given
                sees(core, 0, POSITION_0_0);

                // when
                process(core, create(0, mock(EntityData.class), 0, POSITION_0_0));

                // then
                assertThat(core.entityBoard.allEntities()).hasSize(1);
                verify(eventObserver).receive(any(PlaceEntity.class));
            }
        }

        @Nested
        class MoveEntityTest {

            @Test
            void entity_is_not_moved_when_player_does_not_see_destination() {
                // given
                sees(core, 0, POSITION_0_0);
                core.entityBoard.placeEntity(mockEntity(0, 0), POSITION_0_0);

                // when
                process(core, move(0, new EntityID(0), POSITION_0_1));

                assertThat(core.entityBoard.entitiesAt(POSITION_0_1)).isEmpty();
                verifyNoEvents();
            }

            @Test
            void entity_is_not_moved_when_other_player_has_turn() {
                // given
                sees(core, 1, POSITION_0_0, POSITION_0_1);
                core.entityBoard.placeEntity(mockEntity(0, 1), POSITION_0_0);

                // when
                process(core, move(1, new EntityID(0), POSITION_0_1));

                // then
                assertThat(core.entityBoard.entitiesAt(POSITION_0_1)).isEmpty();
                verifyNoEvents();
            }

            @Test
            void entity_is_not_moved_when_player_does_not_own_it() {
                // given
                sees(core, 0, POSITION_0_0, POSITION_0_1);

                Entity entity = mockEntity(0, 1);
                core.entityBoard.placeEntity(entity, POSITION_0_0);

                // when
                process(core, move(0, new EntityID(0), POSITION_0_1));

                // then
                assertThat(core.entityBoard.entitiesAt(POSITION_0_0)).containsExactly(entity);
                assertThat(core.entityBoard.entitiesAt(POSITION_0_1)).isEmpty();
                verifyNoEvents();
            }

            @Test
            void entity_is_not_moved_when_destination_is_occupied() {
                // given
                sees(core, 0, POSITION_0_0, POSITION_0_1);

                Entity firstEntity = mockEntity(0, 0);
                Entity secondEntity = mockEntity(1, 0);
                core.entityBoard.placeEntity(firstEntity, POSITION_0_0);
                core.entityBoard.placeEntity(secondEntity, POSITION_0_1);

                // when
                process(core, move(0, new EntityID(0), POSITION_0_1));

                assertThat(core.entityBoard.entitiesAt(POSITION_0_0)).containsExactly(firstEntity);
                assertThat(core.entityBoard.entitiesAt(POSITION_0_1)).containsExactly(secondEntity);
                verifyNoEvents();
            }

            @Test
            void entity_is_moved_when_all_conditions_are_met() {
                // given
                sees(core, 0, POSITION_0_0, POSITION_0_1);

                Entity movedEntity = mockEntity(0, 0);
                core.entityBoard.placeEntity(movedEntity, POSITION_0_0);

                // when
                process(core, move(0, new EntityID(0), POSITION_0_1));

                assertThat(core.entityBoard.entitiesAt(POSITION_0_0)).isEmpty();
                assertThat(core.entityBoard.entitiesAt(POSITION_0_1)).containsExactly(movedEntity);

                verify(eventObserver).receive(any(MoveEntity.class));
            }

            @Test
            void player_receives_move_event_when_sees_source_or_destination() {
                // given
                sees(core, 0, POSITION_0_0, POSITION_0_1);
                sees(core, 1, POSITION_0_0);
                sees(core, 2, POSITION_0_1);
                sees(core, 3);

                Entity movedEntity = mockEntity(0, 0);
                core.entityBoard.placeEntity(movedEntity, POSITION_0_0);

                // when
                process(core, move(0, new EntityID(0), POSITION_0_1));

                assertThat(core.entityBoard.entitiesAt(POSITION_0_0)).isEmpty();
                assertThat(core.entityBoard.entitiesAt(POSITION_0_1)).containsExactly(movedEntity);

                verify(eventObserver).receive(any(MoveEntity.class));
                verify(playerEventObservers.get(0).observer()).receive(any(MoveEntity.class));
                verify(playerEventObservers.get(1).observer()).receive(any(MoveEntity.class));
                verify(playerEventObservers.get(2).observer()).receive(any(MoveEntity.class));
                verifyNoInteractions(playerEventObservers.get(3).observer());
            }
        }

        static void sees(GameCore core, long player, Position... positions) {
            for (Position position : positions)
                core.fogOfWar.setVisibility(position, new PlayerID(player), true);
        }
    }

    @Nested
    class TurnActionsTest {
        @Test
        void player_changes_when_current_player_completes_turn() {
            // given
            PlayerID playerBefore = core.playerManager.getCurrentPlayer();

            // when
            process(core, completeTurn(0));

            // then
            PlayerID playerAfter = core.playerManager.getCurrentPlayer();
            Assertions.assertThat(playerBefore).isNotEqualTo(playerAfter);

            playerEventObservers.forEach(o -> verify(o.observer()).receive(completeTurn()));
            verify(eventObserver).receive(completeTurn());
        }

        @Test
        void player_does_not_change_when_not_current_player_completes_turn() {
            // given
            PlayerID playerBefore = core.playerManager.getCurrentPlayer();

            // when
            process(core, completeTurn(1));

            // then
            PlayerID playerAfter = core.playerManager.getCurrentPlayer();
            Assertions.assertThat(playerBefore).isEqualTo(playerAfter);
            verifyNoEvents();
        }
    }

    static Entity mockEntity(long entityID, long playerID) {
        return new Entity(mock(EntityData.class), new EntityID(entityID), new PlayerID(playerID));
    }
}
