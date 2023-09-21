package io.game.world.entity;

import io.animation.Animation;
import io.game.world.WorldEntity;

public abstract class EntityAnimation implements Animation {
    private WorldEntity entity;
    private boolean forceFinish = false;

    public void init(WorldEntity entity) {
        this.entity = entity;
        init();
    }

    public void end() {
        forceFinish = true;
    }

    abstract void init();

    @Override
    public final boolean finished() {
        return _finished() || forceFinish;
    }

    abstract boolean _finished();


    public WorldEntity getEntity() {
        return entity;
    }
}
