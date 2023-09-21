package io.game.world.tile;

import core.model.Position;
import io.game.WorldPosition;
import io.game.world.WorldEntity;
import io.game.world.WorldTexture;

public class AttackMarker extends WorldEntity {
    public AttackMarker(Position position) {
        super(WorldPosition.from(position), WorldTexture.ATTACK_MARKER, 0);
    }
}
