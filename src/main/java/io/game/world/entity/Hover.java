package io.game.world.entity;

import io.game.WorldPosition;

public class Hover extends EntityAnimation {
    private static final float WOBBLES_PER_SECOND = 0.5f;
    private static final float WOBBLES_SIZE = 0.2f;
    private float totalTime = 0;

    @Override
    public void update(float deltaTime) {
        totalTime += deltaTime;
        totalTime = totalTime % (1 / WOBBLES_PER_SECOND);
        var pos = getEntity().getPosition();
        getEntity().position = new WorldPosition(
                pos.x(),
                pos.y(),
                (float) (2 + WOBBLES_SIZE * Math.sin(Math.PI * 2 * totalTime * WOBBLES_PER_SECOND))
        );
    }

    @Override
    public boolean finished() {
        return false;
    }

    @Override
    void init() {

    }
}
