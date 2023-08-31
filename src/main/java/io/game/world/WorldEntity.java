package io.game.world;

import io.game.Camera;
import io.game.WorldPosition;
import io.model.engine.Canvas;

public interface WorldEntity {
    void draw(Canvas canvas, Camera camera);

    WorldPosition getPosition();
}
