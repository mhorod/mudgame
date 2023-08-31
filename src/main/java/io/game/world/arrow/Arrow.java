package io.game.world.arrow;

import io.game.Camera;
import io.game.GamePosition;
import io.game.WorldPosition;
import io.game.world.WorldEntity;
import io.model.engine.Canvas;

public record Arrow(GamePosition position, ArrowKind kind) implements WorldEntity {

    @Override
    public void draw(Canvas canvas, Camera camera) {
        kind().getTexture().draw(getPosition(), canvas, camera);
    }

    @Override
    public WorldPosition getPosition() {
        return new WorldPosition(position().x(), position().y(), 0);
    }
}
