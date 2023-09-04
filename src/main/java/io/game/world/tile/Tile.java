package io.game.world.tile;

import core.model.Position;
import io.game.WorldPosition;
import io.game.world.WorldTexture;
import io.game.world.entity.WorldEntity;

public class Tile extends WorldEntity {
    public static final float ASPECT_RATIO = 256.0f / 148f;

    public Tile(Position position, WorldTexture texture) {
        super(WorldPosition.from(position), texture, false);
    }
}
