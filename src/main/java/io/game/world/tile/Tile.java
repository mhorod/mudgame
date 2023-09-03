package io.game.world.tile;

import core.model.Position;
import io.game.Camera;
import io.game.WorldPosition;
import io.game.world.WorldTexture;
import io.model.engine.Canvas;

public record Tile(Position position, TileKind kind) {
    public static final float ASPECT_RATIO = 256.0f / 148f;

    public void draw(Canvas canvas, Camera camera) {
        switch (kind) {
            case TILE_DARK -> WorldTexture.TILE_DARK.draw(WorldPosition.from(position()), canvas, camera);
            case TILE_LIGHT -> WorldTexture.TILE_LIGHT.draw(WorldPosition.from(position()), canvas, camera);
            case FOG -> WorldTexture.FOG.draw(WorldPosition.from(position()), canvas, camera);
        }
    }
}
