package io.animation;

public abstract class Easer implements Animation {
    private float value;
    private float target;
    private float margin;

    public Easer(float value) {
        this.value = value;
        target = value;
        margin = 0;
    }

    public void setTarget(float target) {
        this.target = target;
        margin = Math.abs(target - value) * 0.01f;
    }

    public float getTarget() {
        return target;
    }

    public abstract void onUpdate(float value);

    @Override
    public void update(float deltaTime) {
        if (value == target) return;
        if (Math.abs(target - value) < margin)
            value = target;
        else
            value += (target - value) * Math.min(1.f, deltaTime * 10);
        onUpdate(value);
    }

    @Override
    public boolean finished() {
        return false;
    }
}
