package core.fogofwar;

import core.entities.model.Entity;
import core.entities.model.EntityData;
import core.entities.model.components.Vision;
import core.model.EntityID;
import core.model.PlayerID;
import core.model.Position;
import lombok.AllArgsConstructor;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

import static core.entities.model.EntityType.PAWN;
import static org.mockito.Mockito.mock;

class PlayerFogOfWarTest {
    @AllArgsConstructor
    private static class PlayerFogOfWarAssert {
        private final PlayerFogOfWar actual;

        PlayerFogOfWarAssert isVisible(int x, int y) {
            return isVisible(new Position(x, y));
        }

        PlayerFogOfWarAssert isVisible(Position position) {
            Assertions.assertThat(actual.isVisible(position)).isTrue();
            return this;
        }

        PlayerFogOfWarAssert isNotVisible(int x, int y) {
            return isNotVisible(new Position(x, y));
        }

        PlayerFogOfWarAssert isNotVisible(Position position) {
            Assertions.assertThat(actual.isVisible(position)).isFalse();
            return this;
        }
    }

    private static PlayerFogOfWarAssert assertThat(PlayerFogOfWar actual) {
        return new PlayerFogOfWarAssert(actual);
    }

    @Test
    void placing_unit_with_vision_makes_area_around_visible() {
        PlayerFogOfWar testee = new PlayerFogOfWar(new PlayerID(0));

        // given
        Entity entity = pawnWithVision(1);
        Position entityPosition = new Position(0, 0);

        // when
        testee.placeEntity(entity, entityPosition);

        // then
        assertThat(testee)
                .isVisible(-1, 1).isVisible(0, 1).isVisible(1, 1)
                .isVisible(-1, 0).isVisible(entityPosition).isVisible(1, 0).isNotVisible(2, 0)
                .isVisible(-1, -1).isVisible(0, -1).isVisible(1, -1);
    }

    @Test
    void removing_unit_makes_area_invisible() {
        PlayerFogOfWar testee = new PlayerFogOfWar(new PlayerID(0));

        // given
        Entity entity = pawnWithVision(1);
        Position entityPosition = new Position(0, 0);

        // when
        testee.placeEntity(entity, entityPosition);
        testee.removeEntity(entity.id());

        // then
        assertThat(testee)
                .isNotVisible(-1, 1).isNotVisible(0, 1).isNotVisible(1, 1)
                .isNotVisible(-1, 0).isNotVisible(entityPosition).isNotVisible(1, 0)
                .isNotVisible(-1, -1).isNotVisible(0, -1).isNotVisible(1, -1);
    }

    @Test
    void removing_unit_does_not_affect_area_seen_by_other_units() {
        PlayerFogOfWar testee = new PlayerFogOfWar(new PlayerID(0));

        // given
        Entity firstEntity = pawnWithVision(1);
        Position firstEntityPosition = new Position(0, 0);

        Entity secondEntity = pawnWithVision(1);
        Position secondEntityPosition = new Position(0, 1);

        // when
        testee.placeEntity(firstEntity, firstEntityPosition);
        testee.placeEntity(secondEntity, secondEntityPosition);
        testee.removeEntity(secondEntity.id());

        // then
        assertThat(testee)
                .isVisible(-1, 1).isVisible(0, 1).isVisible(1, 1)
                .isVisible(-1, 0).isVisible(firstEntityPosition).isVisible(1, 0)
                .isVisible(-1, -1).isVisible(0, -1).isVisible(1, -1);
    }

    @Test
    void moving_unit_changes_visible_area() {
        PlayerFogOfWar testee = new PlayerFogOfWar(new PlayerID(0));

        // given
        Entity entity = pawnWithVision(1);
        Position entityPosition = new Position(0, 0);
        Position destination = new Position(1, 0);

        // when
        testee.placeEntity(entity, entityPosition);
        testee.moveEntity(entity.id(), destination);

        // then
        assertThat(testee)
                .isNotVisible(-1, 1).isVisible(0, 1).isVisible(1, 1).isVisible(2, 1)
                .isNotVisible(-1, 0).isVisible(0, 0).isVisible(1, 0).isVisible(2, 0)
                .isNotVisible(-1, -1).isVisible(0, -1).isVisible(1, -1).isVisible(2, -1);
    }


    Entity pawnWithVision(int range) {
        return new Entity(
                new EntityData(PAWN, List.of(new Vision(range))),
                mock(EntityID.class),
                new PlayerID(0)
        );
    }


}