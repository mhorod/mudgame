package io.game.world.entity;

import io.game.WorldPosition;

public class Shake extends EntityAnimation {
    public static final float TIME = 0.3f;
    private WorldPosition pos;
    private float totalTime = 0;

    @Override
    public void update(float deltaTime) {
        totalTime = Math.min(totalTime + deltaTime, TIME);
        float f = (float) Math.sin(10 * totalTime / TIME) * (1 - totalTime / TIME) * 0.1f;
        getEntity().position = new WorldPosition(
                pos.x() - f,
                pos.y() + f,
                pos.z()
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
