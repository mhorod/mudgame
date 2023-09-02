package io.game.world.entity;

import core.model.EntityID;
import io.game.Camera;
import io.game.WorldPosition;
import io.game.world.WorldTexture;
import io.model.engine.Canvas;
import io.model.engine.Color;

public class Entity extends WorldEntity {
    private final EntityID id;
    private final Color color;

    public Entity(WorldPosition position, EntityID id, Color color) {
        super(position, WorldTexture.BASE, true);
        this.color = color;
        this.id = id;
    }

    @Override
    public void draw(Canvas canvas, Camera camera) {
        super.drawColored(canvas, camera, color);
    }

    public EntityID getId() {
        return id;
    }
}
