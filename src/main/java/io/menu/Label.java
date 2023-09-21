package io.menu;

import io.model.engine.Canvas;
import io.model.engine.TextManager;

public class Label implements UIComponent {

    String text;
    Rectangle bounds;

    public Label(String text) {
        this.text = text;
    }

    @Override
    public float getAspectRatio(TextManager mgr) {
        return 1 / mgr.getTextAspectRatio(text);
    }

    @Override
    public void fitInto(Rectangle rectangle, TextManager mgr) {
        bounds = new Rectangle(getAspectRatio(mgr));
        bounds.fitInto(rectangle);
    }

    @Override
    public void draw(Canvas canvas) {
        canvas.drawText(text, bounds.position, bounds.height);
    }
}
