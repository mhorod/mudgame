package io.game.world.entity;

import java.util.ArrayDeque;
import java.util.List;
import java.util.Queue;

public class AnimationChain extends EntityAnimation {
    private final Queue<EntityAnimation> animations;

    public AnimationChain(List<EntityAnimation> animations) {
        this.animations = new ArrayDeque<>(animations);
    }

    @Override
    public void update(float deltaTime) {
        while (!animations.isEmpty() && animations.peek()._finished()) {
            animations.poll();
            if (!animations.isEmpty())
                animations.peek().init(getEntity());
        }
        if (!animations.isEmpty())
            animations.peek().update(deltaTime);
    }

    @Override
    public boolean _finished() {
        return animations.isEmpty();
    }

    @Override
    void init() {
        if (!animations.isEmpty())
            animations.peek().init(getEntity());
    }
}
