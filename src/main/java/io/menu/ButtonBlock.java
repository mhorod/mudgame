package io.menu;

import io.menu.buttons.Button;
import io.menu.buttons.ButtonMedium;
import io.menu.containers.VBox;
import io.model.ScreenPosition;
import io.model.engine.Canvas;
import io.model.engine.TextManager;

import java.util.List;
import java.util.stream.IntStream;

public class ButtonBlock implements UIComponent {
    List<? extends Button> buttons;

    private final UIComponent content;

    public ButtonBlock(float gap, List<String> text, List<Runnable> handlers) {
        buttons = IntStream.rangeClosed(1, text.size())
                .mapToObj(i -> new ButtonMedium(new Label(text.get(text.size() - i)), handlers.get(handlers.size() - i)))
                .toList();
        content = new VBox(gap, buttons);
    }

    public void update(ScreenPosition mouse, boolean pressed) {
        buttons.forEach(button -> button.update(mouse, pressed));
    }

    public void click(ScreenPosition pos) {
        buttons.forEach(button -> button.click(pos));
    }

    @Override
    public void draw(Canvas canvas) {
        buttons.forEach(button -> button.draw(canvas));
    }


    @Override
    public float getAspectRatio() {
        return content.getAspectRatio();
    }

    @Override
    public void fitInto(Rectangle rect, TextManager mgr) {
        content.fitInto(rect, mgr);
    }

}
