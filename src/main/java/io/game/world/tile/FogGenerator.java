package io.game.world.tile;

import core.model.Position;
import io.game.world.WorldEntity;

public interface FogGenerator {
    public WorldEntity getFog(Position pos);
}
