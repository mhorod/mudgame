package io.menu;

import io.game.GameView;
import io.model.engine.Canvas;
import io.model.engine.TextureBank;
import io.model.input.Input;
import io.model.input.events.Click;
import io.model.input.events.EventHandler;
import io.model.input.events.Scroll;
import io.views.SimpleView;

import java.util.List;

public class MainMenu extends SimpleView implements EventHandler {
    ButtonBlock buttons = new ButtonBlock(0.1f, List.of(
            () -> changeView(new GameView()),
            () -> changeView(new GameView()),
            () -> changeView(new GameView())
    ));

    @Override
    public void draw(Canvas canvas) {
        buttons.draw(canvas);
    }

    @Override
    public void update(Input input, TextureBank bank) {
        input.events().forEach(event -> event.accept(this));
        buttons.fitInto(0, 0, 1, input.window().height() / input.window().width());
        buttons.update(input.mouse().position(), input.mouse().leftPressed());
    }

    @Override
    public void onClick(Click click) {
        buttons.click(click.position());
    }

    @Override
    public void onScroll(Scroll scroll) {

    }
}
