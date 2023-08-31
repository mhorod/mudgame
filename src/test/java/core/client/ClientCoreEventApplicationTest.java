package core.client;

import core.EventTestUtils;
import core.entities.EntityBoardAssert;
import core.entities.components.Component;
import core.entities.model.Entity;
import core.model.PlayerID;
import core.model.Position;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;

import static core.EntityEvents.*;
import static core.EventTestUtils.send;
import static core.turns.TurnEvents.completeTurn;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Test that game core ends up in correct state when receiving events
 */
class ClientCoreEventApplicationTest extends ClientCoreTestBase {
    static final Position POSITION_0_0 = new Position(0, 0);
    static final Position POSITION_0_1 = new Position(0, 1);

    @Nested
    class EntityEventsTest {
        @Test
        void test_create_entity() {
            // given
            List<Component> data = List.of();
            Position position = POSITION_0_0;

            // when
            send(core, create(data, 0, position));

            // then
            assertThat(entityBoard.allEntities()).hasSize(1);
            assertThat(entityBoard.entitiesAt(position)).hasSize(1);
        }

        @Test
        void test_place_entity() {
            // given
            Entity entity = mockEntity(0, 0);
            Position position = POSITION_0_0;

            // when
            send(core, place(entity, position));

            // then
            EntityBoardAssert.assertThat(entityBoard)
                    .containsExactlyEntities(entity)
                    .containsEntityWithId(entity.id());

            assertThat(entityBoard.entitiesAt(position)).containsExactly(entity);
        }

        @Test
        void test_remove_entity() {
            // given
            Entity entity = mockEntity(0, 0);
            Position position = POSITION_0_0;

            // when
            send(core, place(entity, position), remove(entity.id()));

            // then
            EntityBoardAssert.assertThat(entityBoard)
                    .containsNoEntities()
                    .doesNotContainEntityWithId(entity.id());

            assertThat(entityBoard.entitiesAt(position)).isEmpty();
        }

        @Test
        void test_move_entity() {
            // given
            Entity entity = mockEntity(0, 0);
            Position source = POSITION_0_0;
            Position destination = POSITION_0_1;

            // when
            send(core, place(entity, source), move(entity.id(), destination));

            // then
            EntityBoardAssert.assertThat(entityBoard)
                    .containsExactlyEntities(entity)
                    .containsEntityWithId(entity.id());

            assertThat(entityBoard.entitiesAt(source)).isEmpty();
            assertThat(entityBoard.entitiesAt(destination)).containsExactly(entity);
        }
    }

    @Nested
    class TurnEventsTest {

        @Test
        void player_changes_after_completing_turn() {
            // given
            List<PlayerID> players = playerManager.getPlayerIDs();

            // when
            EventTestUtils.send(core, completeTurn());

            // then
            assertThat(playerManager.getCurrentPlayer()).isEqualTo(players.get(1));
        }

        @Test
        void first_player_has_turn_again_when_all_players_complete_turn() {
            // given
            List<PlayerID> players = playerManager.getPlayerIDs();

            // when
            players.forEach(p -> send(core, completeTurn()));

            // then
            assertThat(playerManager.getCurrentPlayer()).isEqualTo(players.get(0));
        }
    }
}
