package io.game.world.entity;

import io.game.WorldPosition;

public class Condense extends EntityAnimation {
    public static final float TIME = 0.5f;
    private WorldPosition pos;
    private float totalTime = 0;

    @Override
    public void update(float deltaTime) {
        totalTime = Math.min(totalTime + deltaTime, TIME);
        getEntity().alpha = totalTime / TIME;
        getEntity().position = new WorldPosition(
                pos.x(),
                pos.y(),
                pos.z() + 1 - totalTime / TIME
        );
    }

    @Override
    public boolean _finished() {
        return totalTime >= TIME;
    }

    @Override
    void init() {
        pos = getEntity().getPosition();
    }
}
