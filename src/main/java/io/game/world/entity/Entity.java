package io.game.world.entity;

import core.model.EntityID;
import io.game.Camera;
import io.game.WorldPosition;
import io.game.world.WorldEntity;
import io.game.world.WorldTexture;
import io.model.engine.Canvas;
import io.model.engine.Color;

import java.util.List;

public class Entity extends WorldEntity {
    private final core.entities.model.Entity entity;

    public Entity(WorldPosition position, core.entities.model.Entity entity) {
        super(position, WorldTexture.from(entity.type()), 1);
        this.entity = entity;
    }

    public List<WorldEntity> withShadow() {
        return List.of(
                new WorldEntity(new WorldPosition(position.x(), position.y(), 0), WorldTexture.SHADOW, 0),
                this
        );
    }

    @Override
    public void draw(Canvas canvas, Camera camera) {
        super.drawColored(canvas, camera, Color.fromPlayerId(entity.owner()));
    }

    public EntityID getId() {
        return entity.id();
    }
}
