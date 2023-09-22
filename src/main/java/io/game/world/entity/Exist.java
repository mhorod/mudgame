package io.game.world.entity;

public class Exist extends EntityAnimation {
    private final float time;
    private float totalTime = 0;

    public Exist(float time) {
        this.time = time;
    }

    @Override
    public void update(float deltaTime) {
        totalTime += deltaTime;
    }

    @Override
    public boolean _finished() {
        return totalTime > time;
    }

    @Override
    void init() {

    }
}
