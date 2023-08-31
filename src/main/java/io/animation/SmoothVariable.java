package io.animation;

public class SmoothVariable {
    private float value;
    private float target;
    private float margin;

    public SmoothVariable(float value) {
        this.value = value;
        target = value;
        margin = 0;
    }

    public float getValue() {
        return value;
    }

    public void setTarget(float target) {
        this.target = target;
        margin = Math.abs(target - value) * 0.01f;
    }

    public void update() {
        if (value != target) {
            if (Math.abs(target - value) < margin)
                value = target;
            else
                value += (target - value) * 0.15f;
        }
    }

    public boolean done() {
        return value == target;
    }
}
