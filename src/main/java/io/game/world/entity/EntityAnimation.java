package io.game.world.entity;

import io.animation.Animation;
import io.game.world.WorldEntity;

public abstract class EntityAnimation implements Animation {
    private WorldEntity entity;

    public void init(WorldEntity entity) {
        this.entity = entity;
        init();
    }

    abstract void init();


    public WorldEntity getEntity() {
        return entity;
    }
}
