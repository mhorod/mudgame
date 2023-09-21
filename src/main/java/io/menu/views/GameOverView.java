package io.menu.views;

import core.model.PlayerID;
import io.menu.Rectangle;
import io.menu.UIComponent;
import io.menu.buttons.Button;
import io.menu.buttons.ButtonMedium;
import io.menu.components.Label;
import io.menu.components.SignedImage;
import io.menu.containers.HBox;
import io.model.engine.Canvas;
import io.model.engine.Color;
import io.model.engine.TextManager;
import io.model.engine.TextureBank;
import io.model.input.Input;
import io.model.input.events.Click;
import io.model.input.events.EventHandler;
import io.model.input.events.Scroll;
import io.model.textures.Texture;
import io.views.SimpleView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class GameOverView extends SimpleView implements EventHandler {

    HBox winners;
    Button goBack = new ButtonMedium(new Label("RETURN"));

    public GameOverView(Map<PlayerID, String> winners) {
        var content = new ArrayList<UIComponent>(List.of(new Label("W:")));
        content.addAll(winners.entrySet().stream().map(entry ->
                new SignedImage(Texture.BASE, Color.fromPlayerId(entry.getKey()),
                        Optional.ofNullable(entry.getValue()).orElse(""))
        ).toList());
        this.winners = new HBox(0.1f, content);
    }

    @Override
    public void draw(Canvas canvas) {
        winners.draw(canvas);
        goBack.draw(canvas);
    }

    @Override
    public void update(Input input, TextureBank bank, TextManager mgr) {
        var window = new Rectangle(0, 0, 1, input.window().height() / input.window().width());
        var scene = new Rectangle(
                window.position.x() + 0.025f,
                window.position.y() + 0.025f,
                window.width() - 0.05f,
                window.height - 0.05f
        );
        winners.fitInto(new Rectangle(
                scene.position.x(),
                scene.position.y() + scene.height * 0.3f,
                scene.width(),
                scene.height * 0.7f
        ), mgr);
        goBack.fitInto(new Rectangle(
                scene.position.x(),
                scene.position.y(),
                scene.width(),
                scene.height * 0.3f
        ), mgr);
        goBack.update(input.mouse().position(), input.mouse().leftPressed());
        input.events().forEach(event -> event.accept(this));
    }

    @Override
    public void onClick(Click click) {
        if (goBack.contains(click.position()))
            changeView(new MainMenu());
    }

    @Override
    public void onScroll(Scroll scroll) {

    }
}
