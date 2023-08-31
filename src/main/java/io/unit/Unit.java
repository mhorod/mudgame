package io.unit;

import java.util.LinkedList;
import java.util.Queue;

public class Unit {
    float x, y, z;
    boolean up = false;
    Queue<UnitAnimation> animationQueue;

    public Unit(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.animationQueue = new LinkedList<>();
    }

    public void update() {
        if (animationQueue.isEmpty()) return;
        var currentAnimation = animationQueue.peek();
        currentAnimation.update();
        if (currentAnimation.done()) {
            animationQueue.remove();
            if (!animationQueue.isEmpty())
                animationQueue.peek().start();
        }
    }

    public void pickUp() {
        animationQueue.add(new PickUp(this));
        if (animationQueue.size() == 1)
            animationQueue.peek().start();
    }

    public void putDown() {
        animationQueue.add(new PutDown(this));
    }

    public UnitPosition getPosition() {
        return new UnitPosition(x, y, z);
    }

    public boolean isUp() {
        return up;
    }
}
