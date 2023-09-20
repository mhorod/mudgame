package io.game.world.entity;

import io.game.WorldPosition;

public class SetZ extends EntityAnimation {
    private final float z;

    public SetZ(float z) {
        this.z = z;
    }

    @Override
    public void update(float deltaTime) {
    }

    @Override
    public boolean finished() {
        return true;
    }

    @Override
    void init() {
        var pos = getEntity().position;
        getEntity().position = new WorldPosition(pos.x(), pos.y(), z);
    }

}
