package io.menu.buttons;

import io.menu.Rectangle;
import io.menu.UIComponent;
import io.model.ScreenPosition;
import io.model.engine.Canvas;
import io.model.engine.TextManager;
import io.model.textures.Texture;
import io.model.textures.TextureDrawData;

public class Button implements UIComponent {
    private final Rectangle bounds;
    private final Runnable onClick;
    private final UIComponent content;

    private final Texture normal, hover, pressed;


    private enum ButtonState {
        NORMAL,
        HOVER,
        PRESSED
    }

    private ButtonState state = ButtonState.NORMAL;

    public Button(UIComponent content, Runnable onClick, Texture normal, Texture hover, Texture pressed) {
        this.content = content;
        this.onClick = onClick;
        this.bounds = new Rectangle(normal.aspectRatio());
        this.normal = normal;
        this.hover = hover;
        this.pressed = pressed;
    }

    @Override
    public float getAspectRatio() {
        return normal.aspectRatio();
    }

    @Override
    public void fitInto(Rectangle rectangle, TextManager mgr) {
        bounds.fitInto(rectangle);
        float boarderSize = rectangle.height * 0.15f;
        Rectangle contentBounds = new Rectangle(
                bounds.position.x() + boarderSize,
                bounds.position.y() + boarderSize,
                bounds.width() - 2 * boarderSize,
                bounds.height - 2 * boarderSize
        );
        content.fitInto(contentBounds, mgr);
    }

    private boolean contains(ScreenPosition position) {
        return bounds.contains(position);
    }

    public void update(ScreenPosition mouse, boolean pressed) {
        if (contains(mouse))
            if (pressed) state = ButtonState.PRESSED;
            else state = ButtonState.HOVER;
        else state = ButtonState.NORMAL;
    }

    public void click(ScreenPosition pos) {
        if (contains(pos))
            onClick.run();
    }

    public void draw(Canvas canvas) {
        switch (state) {
            case NORMAL -> canvas.draw(new TextureDrawData(normal, bounds.position, bounds.height));
            case HOVER -> canvas.draw(new TextureDrawData(hover, bounds.position, bounds.height));
            case PRESSED -> canvas.draw(new TextureDrawData(pressed, bounds.position, bounds.height));
        }
        content.draw(canvas);
    }
}
