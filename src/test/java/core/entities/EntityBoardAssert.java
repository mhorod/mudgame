package core.entities;

import core.entities.model.Entity;
import core.model.EntityID;
import org.assertj.core.api.AbstractAssert;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class EntityBoardAssert extends AbstractAssert<EntityBoardAssert, EntityBoard> {
    protected EntityBoardAssert(EntityBoard entityBoard) {
        super(entityBoard, EntityBoardAssert.class);
    }

    public static EntityBoardAssert assertThatEntityBoard(EntityBoard actual) {
        return new EntityBoardAssert(actual);
    }

    public EntityBoardAssert containsEntityWithId(EntityID entityID) {
        isNotNull();
        if (!actual.containsEntity(entityID))
            failWithMessage("Expecting actual: {} to contain entity with entityID {}", actual,
                            entityID);
        return this;
    }

    public EntityBoardAssert doesNotContainEntityWithId(EntityID entityID) {
        isNotNull();
        if (actual.containsEntity(entityID))
            failWithMessage("Expecting actual: {} not to contain entity with entityID {}", actual,
                            entityID);
        return this;
    }


    public EntityBoardAssert containsNoEntities() {
        isNotNull();
        assertThat(actual.allEntities()).isEmpty();
        return this;
    }

    public EntityBoardAssert containsExactlyEntities(Entity... entities) {
        isNotNull();
        assertThat(actual.allEntities()).containsExactly(entities);
        return this;
    }

    public EntityBoardAssert isEqualTo(EntityBoard expected) {
        isNotNull();
        List<Entity> actualEntities = actual.allEntities();
        List<Entity> expectedEntities = expected.allEntities();
        assertThat(actualEntities).isEqualTo(expectedEntities);
        assertThat(actualEntities)
                .allMatch(
                        e -> actual.entityPosition(e.id()).equals(expected.entityPosition(e.id())));

        assertThat(actual.occupiedPositions()).isEqualTo(expected.occupiedPositions());
        assertThat(actual).isEqualTo(expected);
        return this;
    }
}
