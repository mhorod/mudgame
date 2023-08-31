package io.animation;

import java.util.ArrayList;

public class AnimationController {
    ArrayList<Animation> animations = new ArrayList<>();

    public void addAnimation(Animation animation) {
        animations.add(animation);
    }

    public void update(float deltaTime) {
        animations.forEach(animation -> animation.update(deltaTime));
        animations.removeIf(Animation::finished);
    }
}
