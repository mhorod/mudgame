package io.menu;

import io.model.engine.Canvas;
import io.model.engine.TextManager;
import io.model.textures.Texture;
import io.model.textures.TextureDrawData;

public class Image implements UIComponent {
    private final Rectangle bounds;
    private final Texture texture;

    public Image(Texture texture) {
        bounds = new Rectangle(texture.aspectRatio());
        this.texture = texture;
    }

    @Override
    public float getAspectRatio() {
        return texture.aspectRatio();
    }

    @Override
    public void fitInto(Rectangle rectangle, TextManager mgr) {
        bounds.fitInto(rectangle);
    }

    @Override
    public void draw(Canvas canvas) {
        canvas.draw(new TextureDrawData(texture, bounds.position, bounds.height));
    }
}
