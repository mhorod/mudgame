package io.game.world.tile;

import core.model.Position;
import io.game.Camera;
import io.game.WorldPosition;
import io.game.world.WorldEntity;
import io.game.world.WorldTexture;
import io.model.engine.Canvas;

public record Tile(Position position, TileKind kind) implements WorldEntity {
    public static final float ASPECT_RATIO = 128.0f / 74.0f;

    @Override
    public void draw(Canvas canvas, Camera camera) {
        switch (kind) {
            case TILE_DARK -> WorldTexture.TILE_DARK.draw(getPosition(), canvas, camera);
            case TILE_LIGHT -> WorldTexture.TILE_LIGHT.draw(getPosition(), canvas, camera);
            case FOG -> WorldTexture.FOG.draw(getPosition(), canvas, camera);
        }
    }

    @Override
    public WorldPosition getPosition() {
        return new WorldPosition(position.x(), position.y(), 0);
    }
}
