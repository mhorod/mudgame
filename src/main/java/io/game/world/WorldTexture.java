package io.game.world;

import io.game.Camera;
import io.game.WorldPosition;
import io.model.ScreenPosition;
import io.model.engine.Canvas;
import io.model.textures.Texture;
import io.model.textures.TextureDrawData;

public enum WorldTexture {

    TILE_DARK(Texture.TILE_DARK, new Center(0.5f, 0.58f), 1.0f),
    TILE_LIGHT(Texture.TILE_LIGHT, new Center(0.5f, 0.58f), 1.0f),
    FOG(Texture.FOG, new Center(0.5f, 0.5f), 1.15f),
    SHADOW(Texture.SHADOW, new Center(0.5f, 0.5f), 1.0f),
    UNIT(Texture.UNIT, new Center(0.5f, 0.37f), 1.0f),
    ARROW_NONE(Texture.ARROW_NONE, new Center(0.5f, 0.5f), 1.0f),
    ARROW_SW_NE(Texture.ARROW_SW_NE, new Center(0.5f, 0.5f), 1.0f),
    ARROW_SE_NW(Texture.ARROW_SE_NW, new Center(0.5f, 0.5f), 1.0f),
    ARROW_START_NE(Texture.ARROW_START_NE, new Center(0.5f, 0.5f), 1.0f),
    ARROW_START_SE(Texture.ARROW_START_SE, new Center(0.5f, 0.5f), 1.0f),
    ARROW_START_NW(Texture.ARROW_START_NW, new Center(0.5f, 0.5f), 1.0f),
    ARROW_START_SW(Texture.ARROW_START_SW, new Center(0.5f, 0.5f), 1.0f),
    ARROW_END_NE(Texture.ARROW_END_NE, new Center(0.5f, 0.5f), 1.0f),
    ARROW_END_SE(Texture.ARROW_END_SE, new Center(0.5f, 0.5f), 1.0f),
    ARROW_END_NW(Texture.ARROW_END_NW, new Center(0.5f, 0.5f), 1.0f),
    ARROW_END_SW(Texture.ARROW_END_SW, new Center(0.5f, 0.5f), 1.0f),
    ARROW_SE_NE(Texture.ARROW_SE_NE, new Center(0.5f, 0.5f), 1.0f),
    ARROW_SW_SE(Texture.ARROW_SW_SE, new Center(0.5f, 0.5f), 1.0f),
    ARROW_SW_NW(Texture.ARROW_SW_NW, new Center(0.5f, 0.5f), 1.0f),
    ARROW_NW_NE(Texture.ARROW_NW_NE, new Center(0.5f, 0.5f), 1.0f);
    private final Texture texture;
    private final Center center;
    private final float width;

    WorldTexture(Texture texture, Center center, float width) {
        this.texture = texture;
        this.center = center;
        this.width = width;
    }

    public TextureDrawData getDrawData(WorldPosition position, Camera camera) {
        var centerPosition = camera.convert(position);
        return new TextureDrawData(
                texture,
                new ScreenPosition(
                        centerPosition.x() - width * center.x() * camera.getTileWidth(),
                        centerPosition.y() - width * center.y() * camera.getTileWidth() * texture.aspectRatio()
                ),
                camera.getTileWidth() * width * texture.aspectRatio()
        );
    }

    public void draw(WorldPosition position, Canvas canvas, Camera camera) {
        canvas.draw(getDrawData(position, camera));
    }
}