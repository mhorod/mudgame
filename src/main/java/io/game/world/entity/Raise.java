package io.game.world.entity;

import io.animation.Easer;
import io.game.WorldPosition;

public class Raise extends EntityAnimation {
    private Easer easer;

    @Override
    public void update(float deltaTime) {
        easer.update(deltaTime);
    }

    @Override
    public boolean _finished() {
        return easer.finished();
    }

    @Override
    void init() {
        var pos = getEntity().getPosition();
        easer = new Easer(pos.z()) {
            @Override
            public void onUpdate(float value) {
                getEntity().position = new WorldPosition(pos.x(), pos.y(), value);
            }
        };
        easer.setTarget(2);
    }
}
