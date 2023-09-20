package io.animation;

import java.util.ArrayList;
import java.util.List;

public class AnimationController<T extends Animation> {
    ArrayList<T> animations = new ArrayList<>();

    public void addAnimation(T animation) {
        animations.add(animation);
    }

    public List<T> getAnimations() {
        return animations;
    }

    public void update(float deltaTime) {
        animations.forEach(animation -> animation.update(deltaTime));
        animations.removeIf(Animation::finished);
    }

    public boolean allFinished() {
        return animations.isEmpty();
    }
}
