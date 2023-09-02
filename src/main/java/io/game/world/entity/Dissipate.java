package io.game.world.entity;

import io.game.WorldPosition;

public class Dissipate extends EntityAnimation {
    private static final float TIME = 0.5f;
    private WorldPosition pos;
    private float totalTime = 0;

    @Override
    public void update(float deltaTime) {
        totalTime = Math.min(totalTime + deltaTime, TIME);
        getEntity().alpha = 1 - totalTime / TIME;
        getEntity().position = new WorldPosition(
                pos.x(),
                pos.y(),
                pos.z() + totalTime / TIME
        );
    }

    @Override
    public boolean finished() {
        return totalTime >= TIME;
    }

    @Override
    void init() {
        pos = getEntity().getPosition();
    }
}
