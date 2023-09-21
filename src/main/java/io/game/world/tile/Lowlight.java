package io.game.world.tile;

import core.model.Position;
import io.game.WorldPosition;
import io.game.world.WorldEntity;
import io.game.world.WorldTexture;

public class Lowlight extends WorldEntity {
    public Lowlight(Position position) {
        super(WorldPosition.from(position), WorldTexture.TILE_HIGHLIGHT, -1);
    }
}
