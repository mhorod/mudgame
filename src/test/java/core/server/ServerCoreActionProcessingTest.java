package core.server;

import core.entities.events.MoveEntity;
import core.entities.events.PlaceEntity;
import core.entities.model.Entity;
import core.entities.model.EntityData;
import core.events.PlayerEventObserver;
import core.model.EntityID;
import core.model.PlayerID;
import core.model.Position;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static core.EntityActions.create;
import static core.EntityActions.move;
import static core.EventTestUtils.process;
import static core.entities.EntityBoardAssert.assertThat;
import static core.turns.TurnEvents.completeTurn;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

/**
 * Test that game core ends up in correct state and sends appropriate events.
 * when receiving actions
 */
class ServerCoreActionProcessingTest extends ServerCoreTestBase {
    static final Position POSITION_0_0 = new Position(0, 0);
    static final Position POSITION_0_1 = new Position(0, 1);


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
                assertThat(entityBoard).containsNoEntities();
                verifyNoEvents();
            }

            @Test
            void entity_is_not_created_when_player_does_not_own_it() {
                // given
                sees(0, POSITION_0_0);

                // when
                process(core, create(0, mock(EntityData.class), 1, POSITION_0_0));

                // then
                assertThat(entityBoard).containsNoEntities();
                verifyNoEvents();
            }

            @Test
            void entity_is_not_created_when_position_is_occupied() {
                // given
                sees(0, POSITION_0_0);
                Entity entity = mock(Entity.class);
                entityBoard.placeEntity(entity, POSITION_0_0);

                // when
                process(core, create(0, mock(EntityData.class), 0, POSITION_0_0));

                // then
                assertThat(entityBoard).containsExactlyEntities(entity);
                verifyNoEvents();
            }

            @Test
            void entity_is_not_created_when_other_player_has_turn() {
                // given
                sees(0, POSITION_0_0);

                // when
                process(core, create(1, mock(EntityData.class), 1, POSITION_0_0));

                // then
                assertThat(entityBoard).containsNoEntities();
                verifyNoEvents();
            }

            @Test
            void entity_is_created_when_all_conditions_are_met() {
                // given
                sees(0, POSITION_0_0);

                // when
                process(core, create(0, mock(EntityData.class), 0, POSITION_0_0));

                // then
                assertThat(entityBoard.allEntities()).hasSize(1);
                verify(eventObserver).receive(any(PlaceEntity.class));
            }
        }

        @Nested
        class MoveEntityTest {

            @Test
            void entity_is_not_moved_when_player_does_not_see_destination() {
                // given
                sees(0, POSITION_0_0);
                entityBoard.placeEntity(mockEntity(0, 0), POSITION_0_0);

                // when
                process(core, move(0, new EntityID(0), POSITION_0_1));

                assertThat(entityBoard.entitiesAt(POSITION_0_1)).isEmpty();
                verifyNoEvents();
            }

            @Test
            void entity_is_not_moved_when_other_player_has_turn() {
                // given
                sees(1, POSITION_0_0, POSITION_0_1);
                entityBoard.placeEntity(mockEntity(0, 1), POSITION_0_0);

                // when
                process(core, move(1, new EntityID(0), POSITION_0_1));

                // then
                assertThat(entityBoard.entitiesAt(POSITION_0_1)).isEmpty();
                verifyNoEvents();
            }

            @Test
            void entity_is_not_moved_when_player_does_not_own_it() {
                // given
                sees(0, POSITION_0_0, POSITION_0_1);

                Entity entity = mockEntity(0, 1);
                entityBoard.placeEntity(entity, POSITION_0_0);

                // when
                process(core, move(0, new EntityID(0), POSITION_0_1));

                // then
                assertThat(entityBoard.entitiesAt(POSITION_0_0)).containsExactly(entity);
                assertThat(entityBoard.entitiesAt(POSITION_0_1)).isEmpty();
                verifyNoEvents();
            }

            @Test
            void entity_is_not_moved_when_destination_is_occupied() {
                // given
                sees(0, POSITION_0_0, POSITION_0_1);

                Entity firstEntity = mockEntity(0, 0);
                Entity secondEntity = mockEntity(1, 0);
                entityBoard.placeEntity(firstEntity, POSITION_0_0);
                entityBoard.placeEntity(secondEntity, POSITION_0_1);

                // when
                process(core, move(0, new EntityID(0), POSITION_0_1));

                assertThat(entityBoard.entitiesAt(POSITION_0_0)).containsExactly(firstEntity);
                assertThat(entityBoard.entitiesAt(POSITION_0_1)).containsExactly(secondEntity);
                verifyNoEvents();
            }

            @Test
            void entity_is_moved_when_all_conditions_are_met() {
                // given
                sees(0, POSITION_0_0, POSITION_0_1);

                Entity movedEntity = mockEntity(0, 0);
                entityBoard.placeEntity(movedEntity, POSITION_0_0);

                // when
                process(core, move(0, new EntityID(0), POSITION_0_1));

                assertThat(entityBoard.entitiesAt(POSITION_0_0)).isEmpty();
                assertThat(entityBoard.entitiesAt(POSITION_0_1)).containsExactly(movedEntity);

                verify(eventObserver).receive(any(MoveEntity.class));
            }

            @Test
            void player_receives_move_event_when_sees_source_or_destination() {
                // given
                sees(0, POSITION_0_0, POSITION_0_1);
                sees(1, POSITION_0_0);
                sees(2, POSITION_0_1);
                sees(3);

                Entity movedEntity = mockEntity(0, 0);
                entityBoard.placeEntity(movedEntity, POSITION_0_0);

                // when
                process(core, move(0, new EntityID(0), POSITION_0_1));

                assertThat(entityBoard.entitiesAt(POSITION_0_0)).isEmpty();
                assertThat(entityBoard.entitiesAt(POSITION_0_1)).containsExactly(movedEntity);

                verify(eventObserver).receive(any(MoveEntity.class));
                verify(playerEventObservers.get(0).observer()).receive(any(MoveEntity.class));
                verify(playerEventObservers.get(1).observer()).receive(any(MoveEntity.class));
                verify(playerEventObservers.get(2).observer()).receive(any(MoveEntity.class));
                verifyNoInteractions(playerEventObservers.get(3).observer());
            }
        }

        void sees(long player, Position... positions) {
            for (Position position : positions)
                fogOfWar.setVisibility(position, new PlayerID(player), true);
        }
    }

    @Nested
    class TurnActionsTest {
        @Test
        void player_changes_when_current_player_completes_turn() {
            // given
            PlayerID playerBefore = playerManager.getCurrentPlayer();

            // when
            process(core, completeTurn(0));

            // then
            PlayerID playerAfter = playerManager.getCurrentPlayer();
            assertThat(playerBefore).isNotEqualTo(playerAfter);

            playerEventObservers.forEach(o -> verify(o.observer()).receive(completeTurn()));
            verify(eventObserver).receive(completeTurn());
        }

        @Test
        void player_does_not_change_when_not_current_player_completes_turn() {
            // given
            PlayerID playerBefore = playerManager.getCurrentPlayer();

            // when
            process(core, completeTurn(1));

            // then
            PlayerID playerAfter = playerManager.getCurrentPlayer();
            assertThat(playerBefore).isEqualTo(playerAfter);
            verifyNoEvents();
        }
    }
}
