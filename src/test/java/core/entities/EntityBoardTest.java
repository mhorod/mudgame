package core.entities;


import core.entities.components.Component;
import core.entities.model.Entity;
import core.model.EntityID;
import core.model.PlayerID;
import core.model.Position;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;

import static core.entities.EntityBoardAssert.assertThat;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;


class EntityBoardTest {
    static final List<Component> COMPONENTS = List.of();
    static final PlayerID OWNER = new PlayerID(0);
    static final Position POSITION_0 = new Position(0, 0);
    static final Position POSITION_1 = new Position(0, 1);

    EntityBoard board;

    @BeforeEach
    void init() {
        board = new EntityBoard();
    }

    @Test
    void initial_board_contains_no_entities() {
        assertThat(board).containsNoEntities();
    }

    @Nested
    class CreateTest {

        @Test
        void board_creates_entity_with_given_arguments() {
            // given
            List<Component> data = COMPONENTS;
            PlayerID owner = OWNER;

            // when
            Entity entity = board.createEntity(data, owner, POSITION_0);

            // then
            assertThat(entity.components()).isEqualTo(data);
            assertThat(entity.owner()).isEqualTo(owner);
        }

        @Test
        void board_contains_created_entity() {
            // when
            Entity entity = board.createEntity(COMPONENTS, OWNER, POSITION_0);

            // then
            assertThat(board).containsExactlyEntities(entity).containsEntityWithId(entity.id());
        }

        @Test
        void board_creates_entity_at_given_position() {
            // given
            Position position = POSITION_0;

            // when
            Entity entity = board.createEntity(COMPONENTS, OWNER, position);

            // then
            assertThat(board.entitiesAt(position)).containsExactly(entity);
            assertThat(board.entityPosition(entity.id())).isEqualTo(position);
        }

        @Test
        void board_creates_different_entities_with_same_arguments() {
            // when
            Entity firstEntity = board.createEntity(COMPONENTS, OWNER, POSITION_0);
            Entity secondEntity = board.createEntity(COMPONENTS, OWNER, POSITION_0);

            // then
            assertThat(firstEntity).isNotEqualTo(secondEntity);
            assertThat(board).containsExactlyEntities(firstEntity, secondEntity);
            assertThat(board.entitiesAt(POSITION_0)).containsExactly(firstEntity, secondEntity);
        }
    }

    @Nested
    class PlaceTest {
        @Test
        void placing_same_entity_twice_throws_exception() {
            // given
            Entity entity = new Entity(COMPONENTS, new EntityID(0), OWNER);

            // when
            board.placeEntity(entity, POSITION_1);

            // then
            assertThatThrownBy(() -> board.placeEntity(entity, POSITION_0)).isInstanceOf(
                    EntityIsAlreadyPlaced.class);
        }
    }

    @Nested
    class RemoveTest {
        @Test
        void board_does_not_contain_removed_entity() {
            // given
            Position position = POSITION_0;
            Entity entity = board.createEntity(COMPONENTS, OWNER, position);

            // when
            board.removeEntity(entity.id());

            // then
            assertThat(board).containsNoEntities().doesNotContainEntityWithId(entity.id());
            assertThat(board.entitiesAt(position)).isEmpty();
        }

        @Test
        void removing_same_entity_twice_throws_exception() {
            // given
            Entity entity = board.createEntity(COMPONENTS, OWNER, POSITION_0);

            // when
            board.removeEntity(entity.id());

            // then
            assertThatThrownBy(() -> board.removeEntity(entity.id())).isInstanceOf(
                    EntityDoesNotExist.class);
        }
    }

    @Nested
    class MoveTest {
        @Test
        void board_contains_moved_entity() {
            // given
            Entity entity = board.createEntity(COMPONENTS, OWNER, POSITION_0);

            // when
            board.moveEntity(entity.id(), POSITION_1);

            // then
            assertThat(board).containsEntityWithId(entity.id()).containsExactlyEntities(entity);
        }

        @Test
        void moved_entity_is_on_another_position() {
            // given
            Position from = POSITION_0;
            Position to = POSITION_1;
            Entity entity = board.createEntity(COMPONENTS, OWNER, from);

            // when
            board.moveEntity(entity.id(), to);

            // then
            assertThat(board.entitiesAt(from)).isEmpty();
            assertThat(board.entitiesAt(to)).containsExactly(entity);
        }

        @Test
        void moving_nonexistent_entity_throws_exception() {
            assertThatThrownBy(() -> board.moveEntity(new EntityID(0), POSITION_0)).isInstanceOf(
                    EntityDoesNotExist.class);
        }
    }
}
