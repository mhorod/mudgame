package io.game.world.entity;

import io.game.Camera;
import io.game.WorldPosition;
import io.game.world.WorldTexture;
import io.model.ScreenPosition;
import io.model.engine.Canvas;
import io.model.engine.TextureBank;

public class WorldEntity {
    public WorldPosition position;
    public float alpha = 1;
    private final WorldTexture texture;
    private final boolean hasShadow;

    public WorldEntity(WorldPosition position, WorldTexture texture, boolean hasShadow) {
        this.position = position;
        this.texture = texture;
        this.hasShadow = hasShadow;
    }

    public boolean contains(ScreenPosition position, TextureBank bank, Camera camera) {
        return bank.contains(WorldTexture.UNIT.getDrawData(getPosition(), camera), position);
    }

    public void draw(Canvas canvas, Camera camera) {
        if (alpha == 1)
            texture.draw(getPosition(), canvas, camera);
        else
            texture.drawTransparent(getPosition(), canvas, camera, alpha);
    }

    public void drawShadow(Canvas canvas, Camera camera) {
        if (hasShadow)
            WorldTexture.SHADOW.draw(new WorldPosition(position.x(), position.y(), 0), canvas, camera);
    }


    public WorldPosition getPosition() {
        return position;
    }
}
