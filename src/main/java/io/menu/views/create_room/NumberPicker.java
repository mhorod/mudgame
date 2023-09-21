package io.menu.views.create_room;

import io.animation.Animation;
import io.animation.Easer;
import io.menu.Label;
import io.menu.Rectangle;
import io.menu.UIComponent;
import io.menu.scroll.ScrollBar;
import io.model.engine.Canvas;
import io.model.engine.TextManager;

public class NumberPicker implements UIComponent, Animation {
    ScrollBar scrollBar;
    Label valueLabel;

    Easer barEaser;
    int number;
    private final int maxNumber;
    private final int minNumber;

    public NumberPicker(int minNumber, int maxNumber) {
        this.minNumber = minNumber;
        this.maxNumber = maxNumber;
        this.number = minNumber;

        this.scrollBar = new ScrollBar(maxNumber - minNumber, 0.3f, 0f);
        this.valueLabel = new Label(String.valueOf(minNumber));
        barEaser = new Easer(0) {
            @Override
            public void onUpdate(float value) {
                scrollBar.scrollAmount = value;
            }
        };
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
        this.valueLabel = new Label(String.valueOf(number));
        barEaser.setTarget(number - minNumber);
    }

    @Override
    public float getAspectRatio(TextManager mgr) {
        return 5;
    }

    @Override
    public void fitInto(Rectangle rectangle, TextManager mgr) {
        Rectangle bounds = new Rectangle(getAspectRatio(mgr));
        bounds.fitInto(rectangle);

        valueLabel.fitInto(new Rectangle(
                bounds.position.x(),
                bounds.position.y(),
                bounds.width(),
                bounds.width() * 0.95f
        ), mgr);

        scrollBar.fitInto(new Rectangle(
                bounds.position.x(),
                bounds.position.y() + bounds.width(),
                bounds.width(),
                bounds.height - bounds.width()
        ), mgr);
    }

    @Override
    public void draw(Canvas canvas) {
        scrollBar.draw(canvas);
        valueLabel.draw(canvas);
    }

    @Override
    public void update(float deltaTime) {
        barEaser.update(deltaTime);
    }

    @Override
    public boolean finished() {
        return false;
    }
}
