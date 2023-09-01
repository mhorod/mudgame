package io.game.world.entity;

import io.animation.Animation;

public abstract class EntityAnimation implements Animation {
    private Entity entity;

    public void init(Entity entity) {
        this.entity = entity;
        init();
    }

    abstract void init();


    public Entity getEntity() {
        return entity;
    }
}
