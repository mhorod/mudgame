package io.animation;

public abstract class Floater implements Animation {
    private float dx, dy;
    private float x, y;

    public void setValue(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public void add(float dx, float dy, float deltaTime) {
        this.dx = dx / deltaTime;
        this.dy = dy / deltaTime;
    }

    @Override
    public final void update(float deltaTime) {
        x += dx * deltaTime;
        y += dy * deltaTime;
        var dampening = Math.pow(0.05, deltaTime);
        dx *= dampening;
        dy *= dampening;
        if (Math.abs(dx) + Math.abs(dy) < 0.001)
            dx = dy = 0;
        onUpdate(x, y);
    }

    @Override
    public final boolean finished() {
        return false;
    }

    protected abstract void onUpdate(float x, float y);
}
