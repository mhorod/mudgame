package core;

import core.entities.EntityBoardAssert;
import core.entities.model.Entity;
import core.entities.model.EntityData;
import core.events.EventSender;
import core.model.PlayerID;
import core.model.Position;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;

import static core.EntityEvents.*;
import static core.EventTestUtils.send;
import static core.GameCoreTest.mockEntity;
import static core.TurnEvents.completeTurn;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

/**
 * Test that game core ends up in correct state when receiving events
 */
class GameCoreEventApplicationTest
{
    static final Position POSITION_0_0 = new Position(0, 0);
    static final Position POSITION_0_1 = new Position(0, 1);
    static final EventSender MOCK_SENDER = mock(EventSender.class);

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
            EntityBoardAssert.assertThat(core.entityBoard)
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
            EntityBoardAssert.assertThat(core.entityBoard)
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
            EntityBoardAssert.assertThat(core.entityBoard)
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
            EventTestUtils.send(core, completeTurn());

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
