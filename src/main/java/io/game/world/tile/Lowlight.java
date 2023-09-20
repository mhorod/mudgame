package io.game.world.tile;

import core.model.Position;
import io.game.WorldPosition;
import io.game.world.WorldTexture;
import io.game.world.entity.WorldEntity;

public class Lowlight extends WorldEntity {
    public Lowlight(Position position) {
        super(WorldPosition.from(position), WorldTexture.TILE_HIGHLIGHT, 0);
    }
}
