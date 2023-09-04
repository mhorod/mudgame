package io.menu;

import io.game.GameView;
import io.model.ScreenPosition;
import io.model.engine.Canvas;
import io.model.engine.TextureBank;
import io.model.input.Input;
import io.model.input.events.Click;
import io.model.input.events.EventHandler;
import io.model.input.events.Scroll;
import io.model.textures.Texture;
import io.model.textures.TextureDrawData;
import io.views.SimpleView;

import java.util.List;

public class MainMenu extends SimpleView implements EventHandler {
    ButtonBlock buttons = new ButtonBlock(0.1f,
            List.of(
                    "PLAY",
                    "SETTINGS",
                    "EXIT"
            ),
            List.of(
                    () -> changeView(new GameView()),
                    () -> changeView(new GameView()),
                    () -> changeView(new GameView())
            ));

    Rectangle logo = new Rectangle(Texture.LOGO.aspectRatio());

    @Override
    public void draw(Canvas canvas) {


        buttons.draw(canvas);
        canvas.draw(new TextureDrawData(
                Texture.LOGO,
                logo.position,
                logo.height
        ));
    }

    @Override
    public void update(Input input, TextureBank bank) {
        var window = new Rectangle(input.window().height() / input.window().width());
        window.position = new ScreenPosition(0, 0);
        window.height = input.window().height() / input.window().width();

        var scene = new Rectangle(0.5f);
        scene.fitInto(window);

        input.events().forEach(event -> event.accept(this));
        buttons.fitInto(scene.position.x(), scene.position.y(), scene.width() / 2, scene.height);
        buttons.update(input.mouse().position(), input.mouse().leftPressed());
        logo.fitInto(new Rectangle(scene.position.x() + scene.width() / 2, scene.position.y(), scene.width() / 2, scene.height));
    }

    @Override
    public void onClick(Click click) {
        buttons.click(click.position());
    }

    @Override
    public void onScroll(Scroll scroll) {

    }
}
