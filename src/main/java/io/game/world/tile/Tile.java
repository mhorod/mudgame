package io.game.world.tile;

import core.model.Position;
import io.game.Camera;
import io.game.WorldPosition;
import io.game.world.WorldEntity;
import io.game.world.WorldTexture;
import io.model.engine.Canvas;
import io.model.engine.Color;

public class Tile extends WorldEntity {
    public static final float ASPECT_RATIO = 256.0f / 148f;
    private final Position gamePosition;
    private final Color color;

    public Tile(Position position, WorldTexture texture, Color color) {
        super(WorldPosition.from(position), texture, -2);
        this.gamePosition = position;
        this.color = color;
    }

    @Override
    public void draw(Canvas canvas, Camera camera) {
        super.drawColored(canvas, camera, color);
    }

    public Position getGamePosition() {
        return gamePosition;
    }
}

