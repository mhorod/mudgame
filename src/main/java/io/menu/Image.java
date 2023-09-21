package io.menu;

import io.model.engine.Canvas;
import io.model.engine.Color;
import io.model.engine.TextManager;
import io.model.textures.Texture;
import io.model.textures.TextureDrawData;

public class Image implements UIComponent {
    private final Rectangle bounds;
    private final Texture texture;
    private final Color color;

    public Image(Texture texture) {
        bounds = new Rectangle(texture.aspectRatio());
        this.color = Color.WHITE;
        this.texture = texture;
    }

    public Image(Texture texture, Color color) {
        bounds = new Rectangle(texture.aspectRatio());
        this.color = color;
        this.texture = texture;
    }

    @Override
    public float getAspectRatio(TextManager mgr) {
        return texture.aspectRatio();
    }

    @Override
    public void fitInto(Rectangle rectangle, TextManager mgr) {
        bounds.fitInto(rectangle);
    }

    public Rectangle getBounds() {
        return bounds;
    }

    @Override
    public void draw(Canvas canvas) {
        canvas.drawColored(new TextureDrawData(texture, bounds.position, bounds.height), 1, color);
    }
}
