package io.game.world.entity;

import core.model.EntityID;
import io.game.Camera;
import io.game.WorldPosition;
import io.game.world.WorldTexture;
import io.model.engine.Canvas;
import io.model.engine.Color;

public class Entity extends WorldEntity {
    private final core.entities.model.Entity entity;

    public Entity(WorldPosition position, core.entities.model.Entity entity) {
        super(position, WorldTexture.from(entity.type()), true);
        this.entity = entity;
    }

    @Override
    public void draw(Canvas canvas, Camera camera) {
        super.drawColored(canvas, camera, Color.fromPlayerId(entity.owner()));
    }

    public EntityID getId() {
        return entity.id();
    }
}
