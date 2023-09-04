package io.game.world.tile;

import core.model.Position;
import io.game.WorldPosition;
import io.game.world.WorldTexture;
import io.game.world.entity.WorldEntity;

public class Tile extends WorldEntity {
    public static final float ASPECT_RATIO = 256.0f / 148f;

    public Tile(Position position, TileKind kind) {
        super(WorldPosition.from(position), switch (kind) {
            case TILE_DARK -> WorldTexture.TILE_DARK;
            case TILE_LIGHT -> WorldTexture.TILE_LIGHT;
            case FOG -> WorldTexture.FOG;
            case FOG_TALL -> WorldTexture.FOG_TALL;
        }, false);
    }
}
