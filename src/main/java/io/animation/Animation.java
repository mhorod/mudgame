package io.animation;

public interface Animation {
    void update(float deltaTime);

    boolean finished();
}
