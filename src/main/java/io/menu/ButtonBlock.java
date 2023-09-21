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
    private final List<? extends Button> buttons;
    private final List<Runnable> handlers;

    private final UIComponent content;

    public ButtonBlock(float gap, List<String> text, List<Runnable> handlers) {
        buttons = IntStream.rangeClosed(1, text.size())
                .mapToObj(i -> new ButtonMedium(new Label(text.get(text.size() - i))))
                .toList();
        this.handlers = handlers;
        content = new VBox(gap, buttons);
    }

    public void update(ScreenPosition mouse, boolean pressed) {
        buttons.forEach(button -> button.update(mouse, pressed));
    }

    public void click(ScreenPosition pos) {
        for (int i = 0; i < buttons.size(); i++)
            if (buttons.get(i).contains(pos))
                handlers.get(i).run();
    }

    @Override
    public void draw(Canvas canvas) {
        buttons.forEach(button -> button.draw(canvas));
    }


    @Override
    public float getAspectRatio(TextManager mgr) {
        return content.getAspectRatio(mgr);
    }

    @Override
    public void fitInto(Rectangle rect, TextManager mgr) {
        content.fitInto(rect, mgr);
    }

}
