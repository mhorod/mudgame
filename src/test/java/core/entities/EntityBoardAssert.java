package core.entities;

import core.entities.model.Entity;
import core.model.EntityID;
import org.assertj.core.api.AbstractAssert;
import org.assertj.core.api.Assertions;

public class EntityBoardAssert extends AbstractAssert<EntityBoardAssert, EntityBoard> {
    protected EntityBoardAssert(EntityBoard entityBoard) {
        super(entityBoard, EntityBoardAssert.class);
    }

    public static EntityBoardAssert assertThat(EntityBoard actual) {
        return new EntityBoardAssert(actual);
    }

    public EntityBoardAssert containsEntityWithId(EntityID entityID) {
        isNotNull();
        if (!actual.containsEntity(entityID))
            failWithMessage("Expecting actual: {} to contain entity with id {}", actual, entityID);
        return this;
    }

    public EntityBoardAssert doesNotContainEntityWithId(EntityID entityID) {
        isNotNull();
        if (actual.containsEntity(entityID))
            failWithMessage("Expecting actual: {} not to contain entity with id {}", actual,
                            entityID);
        return this;
    }


    public EntityBoardAssert containsNoEntities() {
        isNotNull();
        Assertions.assertThat(actual.allEntities()).isEmpty();
        return this;
    }

    public EntityBoardAssert containsExactlyEntities(Entity... entities) {
        isNotNull();
        Assertions.assertThat(actual.allEntities()).containsExactly(entities);
        return this;
    }
}
