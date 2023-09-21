package io.menu.views;

import core.model.PlayerID;
import io.menu.Label;
import io.menu.Rectangle;
import io.menu.UIComponent;
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

public class GameOverView extends SimpleView implements EventHandler {

    HBox winners;

    public GameOverView(Map<PlayerID, String> winners) {
        var content = new ArrayList<UIComponent>(List.of(new Label("W:")));
        content.addAll(winners.entrySet().stream().map(entry ->
                new SignedImage(Texture.BASE, Color.fromPlayerId(entry.getKey()), entry.getValue())
        ).toList());
        this.winners = new HBox(0.1f, content);
    }

    @Override
    public void draw(Canvas canvas) {
        winners.draw(canvas);
    }

    @Override
    public void update(Input input, TextureBank bank, TextManager mgr) {
        var window = new Rectangle(0, 0, 1, input.window().width() / input.window().height());
        winners.fitInto(window, mgr);

    }

    @Override
    public void onClick(Click click) {

    }

    @Override
    public void onScroll(Scroll scroll) {

    }
}
