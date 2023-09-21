package io.game.world;

import io.game.Camera;
import io.game.WorldPosition;
import io.model.ScreenPosition;
import io.model.engine.Canvas;
import io.model.engine.Color;
import io.model.engine.TextureBank;

public class WorldEntity {
    public WorldPosition position;
    public float alpha = 1;
    private final WorldTexture texture;
    private final int layer;

    public WorldEntity(WorldPosition position, WorldTexture texture, int layer) {
        this.position = position;
        this.texture = texture;
        this.layer = layer;
    }

    public boolean contains(ScreenPosition position, TextureBank bank, Camera camera) {
        return bank.contains(texture.getDrawData(getPosition(), camera), position);
    }

    public void draw(Canvas canvas, Camera camera) {
        if (alpha == 1)
            texture.draw(getPosition(), canvas, camera);
        else
            texture.drawColored(getPosition(), canvas, camera, Color.WHITE, alpha);
    }

    public void drawColored(Canvas canvas, Camera camera, Color color) {
        texture.drawColored(getPosition(), canvas, camera, color, alpha);
    }


    public WorldPosition getPosition() {
        return position;
    }

    public int getLayer() {
        return layer;
    }
}
