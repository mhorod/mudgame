package io.menu;

import io.model.engine.Canvas;
import io.model.engine.TextManager;

public class Label implements UIComponent {

    String text;
    private float aspectRatio;
    Rectangle bounds;

    public Label(String text) {
        this.text = text;
    }

    @Override
    public float getAspectRatio() {
        return aspectRatio;
    }

    @Override
    public void fitInto(Rectangle rectangle, TextManager mgr) {
        aspectRatio = 1 / mgr.getTextAspectRatio(text);
        bounds = new Rectangle(aspectRatio);
        bounds.fitInto(rectangle);
    }

    @Override
    public void draw(Canvas canvas) {
        canvas.drawText(text, bounds.position, bounds.height);
    }
}
