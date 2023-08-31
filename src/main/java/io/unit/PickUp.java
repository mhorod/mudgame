package io.unit;

import io.animation.SmoothVariable;

public class PickUp implements UnitAnimation {
    private final Unit unit;
    private SmoothVariable z;

    PickUp(Unit unit) {
        this.unit = unit;
    }

    @Override
    public void update() {
        z.update();
        unit.z = z.getValue();
    }

    @Override
    public void start() {
        z = new SmoothVariable(unit.z);
        z.setTarget(1);
    }

    @Override
    public boolean done() {
        return z.done();
    }
}
