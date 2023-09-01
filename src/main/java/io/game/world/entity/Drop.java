package io.game.world.entity;

import io.game.WorldPosition;

public class Drop extends EntityAnimation {
    private float totalTime = 0;
    private WorldPosition pos;
    private float fallTime;

    private static final float G = 20;
    private static final float BOUNCE_HEIGHT = 0.5f;
    private static final float BOUNCE_TIME = (float) Math.sqrt(BOUNCE_HEIGHT / G) * 2;

    @Override
    public void update(float deltaTime) {
        totalTime += deltaTime;
        float z;
        if (totalTime < fallTime)
            z = pos.z() - G * totalTime * totalTime;
        else
            z = (float) Math.max(BOUNCE_HEIGHT - G * Math.pow(totalTime - fallTime - BOUNCE_TIME / 2, 2), 0);
        getEntity().position = new WorldPosition(pos.x(), pos.y(), z);
    }

    @Override
    public boolean finished() {
        return totalTime > fallTime + BOUNCE_TIME;
    }

    @Override
    void init() {
        pos = getEntity().getPosition();
        fallTime = (float) Math.sqrt(pos.z() / G);
    }

}
