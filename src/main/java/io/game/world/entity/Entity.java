package io.game.world.entity;

import core.model.EntityID;
import io.game.WorldPosition;
import io.game.world.WorldTexture;

public class Entity extends WorldEntity {
    private final EntityID id;

    public Entity(WorldPosition position, EntityID id) {
        super(position, WorldTexture.BASE, true);
        this.id = id;
    }

    public EntityID getId() {
        return id;
    }
}
