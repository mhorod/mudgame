package io.game.world.entity;

import core.model.Position;
import io.game.WorldPosition;

public class JumpTo extends EntityAnimation {
    private static final float G = 20;
    private static final float SPEED = 3;

    private WorldPosition start;

    private float dx, dy;
    private final Position destination;
    private float totalTime = 0f;

    public JumpTo(Position destination) {
        this.destination = destination;
    }


    @Override
    public void update(float deltaTime) {
        totalTime += deltaTime;
        float t = totalTime * SPEED;
        getEntity().position = new WorldPosition(
                start.x() + t * dx,
                start.y() + t * dy,
                (t - 1) * (-G * t / SPEED - start.z())
        );

    }

    @Override
    public boolean _finished() {
        return totalTime >= 1 / SPEED;
    }

    @Override
    void init() {
        start = getEntity().position;
        dx = destination.x() - start.x();
        dy = destination.y() - start.y();

    }
}
