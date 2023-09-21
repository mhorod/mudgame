package io.menu.components;

import io.menu.Rectangle;
import io.menu.UIComponent;
import io.model.engine.Canvas;
import io.model.engine.Color;
import io.model.engine.TextManager;
import io.model.textures.Texture;

public class SignedImage implements UIComponent {
    private final Texture texture;
    private final Image image;
    private final Label label;

    public SignedImage(Texture texture, String text) {
        this.texture = texture;
        this.image = new Image(texture);
        this.label = new Label(text);
    }

    public SignedImage(Texture texture, Color color, String text) {
        this.texture = texture;
        this.image = new Image(texture, color);
        this.label = new Label(text);
    }

    @Override
    public float getAspectRatio(TextManager mgr) {
        return texture.aspectRatio() + 0.2f;
    }

    @Override
    public void fitInto(Rectangle rectangle, TextManager mgr) {
        var bounds = new Rectangle(getAspectRatio(mgr));
        bounds.fitInto(rectangle);
        label.fitInto(new Rectangle(
                bounds.position.x(),
                bounds.position.y(),
                bounds.width(),
                bounds.width() * 0.2f
        ), mgr);
        image.fitInto(new Rectangle(
                bounds.position.x(),
                bounds.position.y() + 0.2f * bounds.width(),
                bounds.width(),
                bounds.width() * image.getAspectRatio(mgr)
        ), mgr);
    }

    @Override
    public void draw(Canvas canvas) {
        image.draw(canvas);
        label.draw(canvas);
    }
}
