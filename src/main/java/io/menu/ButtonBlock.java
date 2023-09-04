package io.menu;

import io.model.ScreenPosition;
import io.model.engine.Canvas;

import java.util.List;
import java.util.stream.IntStream;

public class ButtonBlock {
    private final float gap;
    List<Button> buttons;

    public ButtonBlock(float gap, List<String> text, List<Runnable> handlers) {
        this.gap = gap;
        buttons = IntStream.rangeClosed(1, text.size())
                .mapToObj(i -> new Button(text.get(text.size() - i), handlers.get(handlers.size() - i)))
                .toList();
    }

    public void fitInto(float x, float y, float width, float height) {
        float noButtons = buttons.size() * (1 + gap) - gap;
        float aspect = noButtons * Button.ASPECT_RATIO;
        float myHeight = Math.min(height, width * aspect);
        var position = new ScreenPosition(
                x + width / 2 - myHeight / aspect / 2,
                y + height / 2 - myHeight / 2
        );
        for (int i = 0; i < buttons.size(); i++) {
            buttons.get(i).position = new ScreenPosition(position.x(), position.y() + i * (myHeight / noButtons) * (1 + gap));
            buttons.get(i).height = myHeight / noButtons;
        }
    }

    public void update(ScreenPosition mouse, boolean pressed) {
        buttons.forEach(button -> button.update(mouse, pressed));
    }

    public void click(ScreenPosition pos) {
        buttons.forEach(button -> button.click(pos));
    }

    public void draw(Canvas canvas) {
        buttons.forEach(button -> button.draw(canvas));
    }


}
