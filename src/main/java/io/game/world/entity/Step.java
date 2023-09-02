package io.game.world.entity;

import io.game.WorldPosition;

public class Step extends EntityAnimation {
    private final WorldPosition from, to;
    private final float duration;
    private float totalTime = 0;

    private static final float STEP_HEIGHT = 0.3f;

    public Step(WorldPosition from, WorldPosition to, float duration) {
        this.duration = duration;
        this.from = from;
        this.to = to;
    }

    @Override
    public void update(float deltaTime) {
        totalTime += deltaTime;
        if (totalTime > duration)
            totalTime = duration;

        var frac = totalTime / duration;
        var x = (1 - frac) * from.x() + frac * to.x();
        var y = (1 - frac) * from.y() + frac * to.y();
        var z = (1 - frac) * from.z() + frac * to.z() + STEP_HEIGHT * (1 - (float) Math.pow(2 * frac - 1, 2));
        getEntity().position = new WorldPosition(x, y, z);
    }

    @Override
    public boolean finished() {
        return totalTime >= duration;
    }

    @Override
    void init() {
    }
}
