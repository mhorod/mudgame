package io.game.world.entity;

import core.model.EntityID;
import io.game.Camera;
import io.game.WorldPosition;
import io.game.world.WorldEntity;
import io.game.world.WorldTexture;
import io.model.ScreenPosition;
import io.model.engine.Canvas;
import io.model.engine.TextureBank;

public class Entity implements WorldEntity {
    public WorldPosition position;
    private final EntityID id;

    public Entity(WorldPosition position, EntityID id) {
        this.position = position;
        this.id = id;
    }

    public EntityID getId() {
        return id;
    }

    public boolean contains(ScreenPosition position, TextureBank bank, Camera camera) {
        return bank.contains(WorldTexture.UNIT.getDrawData(getPosition(), camera), position);
    }

    @Override
    public void draw(Canvas canvas, Camera camera) {
        WorldTexture.UNIT.draw(getPosition(), canvas, camera);
    }

    public void drawShadow(Canvas canvas, Camera camera) {
        WorldTexture.SHADOW.draw(new WorldPosition(position.x(), position.y(), 0), canvas, camera);
    }


    @Override
    public WorldPosition getPosition() {
        return position;
    }
}
