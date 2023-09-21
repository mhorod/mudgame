package io.game.world.tile;

import core.model.Position;
import io.game.WorldPosition;
import io.game.world.WorldEntity;
import io.game.world.WorldTexture;

public class Fog extends WorldEntity {
    private final Position gamePosition;

    public Fog(Position position, WorldTexture texture) {
        super(WorldPosition.from(position), texture, 1);
        this.gamePosition = position;
    }

    public Position getGamePosition() {
        return gamePosition;
    }
}

