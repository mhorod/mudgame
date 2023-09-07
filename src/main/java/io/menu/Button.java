package io.menu;

import io.model.ScreenPosition;
import io.model.engine.Canvas;
import io.model.textures.Texture;
import io.model.textures.TextureDrawData;

public class Button {
    public ScreenPosition position;
    public float height;
    private final Runnable onClick;
    private final String text;


    private enum ButtonState {
        NORMAL,
        HOVER,
        PRESSED
    }

    private ButtonState state = ButtonState.NORMAL;
    public static final float ASPECT_RATIO = Texture.BUTTON_SMALL.aspectRatio();

    public Button(String text, Runnable onClick) {
        this.text = text;
        this.onClick = onClick;
    }

    private boolean contains(ScreenPosition position) {
        return position.x() > this.position.x()
                && position.y() > this.position.y()
                && position.y() < this.position.y() + height
                && position.x() < this.position.x() + height / ASPECT_RATIO;
    }

    public void fitInto(float x, float y, float width, float height) {
        this.height = Math.min(height, width * ASPECT_RATIO);
        this.position = new ScreenPosition(
                x + width / 2 - this.height / ASPECT_RATIO / 2,
                y + height / 2 - this.height / 2
        );
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
            case NORMAL, HOVER -> canvas.draw(new TextureDrawData(Texture.BUTTON_SMALL, position, height));
            case PRESSED -> canvas.draw(new TextureDrawData(Texture.BUTTON_SMALL_PRESSED, position, height));
        }
        float textHeight = 0.4f * height;
        var textWidth = canvas.getTextAspectRatio(text) * textHeight;
        canvas.drawText(text, new ScreenPosition(
                position.x() + height / ASPECT_RATIO / 2 - textWidth / 2,
                position.y() + height / 2 - textHeight / 2
        ), textHeight);
    }
}
