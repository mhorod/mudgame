package io.game.world;

import core.entities.model.EntityType;
import io.game.Camera;
import io.game.WorldPosition;
import io.model.ScreenPosition;
import io.model.engine.Canvas;
import io.model.engine.Color;
import io.model.textures.Texture;
import io.model.textures.TextureDrawData;

public enum WorldTexture {

    TILE_DARK(Texture.TILE_DARK, new Center(0.5f, 0.668f), 1.01f),
    TILE_LIGHT(Texture.TILE_LIGHT, new Center(0.5f, 0.668f), 1.0f),
    TILE_HIGHLIGHT(Texture.TILE_HIGHLIGHT, new Center(0.5f, 0.5f), 1f),
    FOG(Texture.FOG, new Center(0.5f, 0.5f), 1.15f),
    FOG_LEFT(Texture.FOG_LEFT, new Center(0.5f, 0.62f), 1.15f),
    FOG_RIGHT(Texture.FOG_RIGHT, new Center(0.5f, 0.62f), 1.15f),
    FOG_TALL(Texture.FOG_TALL, new Center(0.5f, 0.62f), 1.15f),
    SHADOW(Texture.SHADOW, new Center(0.5f, 0.5f), 1.0f),
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
    ARROW_NW_NE(Texture.ARROW_NW_NE, new Center(0.5f, 0.5f), 1.0f),
    BASE(Texture.BASE, new Center(0.5f, 0.23f), 0.8f),
    TOWER(Texture.TOWER, new Center(0.5f, 0.187f), 0.8f),
    WARRIOR(Texture.WARRIOR, new Center(0.5f, 0.23f), 0.5f),
    MARSH_WIGGLE(Texture.MARSH_WIGGLE, new Center(0.5f, 0.15f), 0.6f),
    PAWN(Texture.PAWN, new Center(0.5f, 0.15f), 0.38f);
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
                        centerPosition.y() -
                                width * center.y() * camera.getTileWidth() * texture.aspectRatio()
                ),
                camera.getTileWidth() * width * texture.aspectRatio()
        );
    }

    public void draw(WorldPosition position, Canvas canvas, Camera camera) {
        canvas.draw(getDrawData(position, camera));
    }

    public void drawColored(
            WorldPosition position, Canvas canvas, Camera camera, Color color, float alpha
    ) {
        canvas.drawColored(getDrawData(position, camera), alpha, color);
    }

    public static WorldTexture from(EntityType type) {
        return switch (type) {
            case PAWN -> PAWN;
            case MARSH_WIGGLE -> MARSH_WIGGLE;
            case BASE -> BASE;
            case WARRIOR -> WARRIOR;
            case TOWER -> TOWER;
        };
    }
}
