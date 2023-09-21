package io.menu.views.create_room;

import core.model.PlayerID;
import io.menu.Image;
import io.menu.Rectangle;
import io.menu.UIComponent;
import io.menu.buttons.Button;
import io.menu.buttons.ButtonSmall;
import io.menu.containers.HBox;
import io.model.ScreenPosition;
import io.model.engine.Canvas;
import io.model.engine.Color;
import io.model.engine.TextManager;
import io.model.textures.Texture;

import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.IntStream;

public class ColorPicker implements UIComponent {
    private HBox content;
    private List<? extends Button> buttons;

    public ColorPicker(int noPlayers) {
        setNumberOfPlayers(noPlayers);
    }

    public void setNumberOfPlayers(int noPlayers) {
        buttons = IntStream.range(0, noPlayers)
                .mapToObj(PlayerID::new)
                .map(Color::fromPlayerId)
                .map(color -> new ButtonSmall(new Image(Texture.BASE, color)))
                .toList();
        content = new HBox(0.1f, buttons);
    }

    public void setLocked(Collection<PlayerID> lockedIds) {
        for (int i = 0; i < buttons.size(); i++) {
            buttons.get(i).setPressed(lockedIds.contains(new PlayerID(i)));
        }
    }

    public void update(ScreenPosition mouse, boolean pressed) {
        buttons.forEach(button -> button.update(mouse, pressed));
    }

    public void click(ScreenPosition position, Consumer<PlayerID> consumer) {
        for (int i = 0; i < buttons.size(); i++) {
            if (buttons.get(i).contains(position))
                consumer.accept(new PlayerID(i));
        }
    }

    @Override
    public float getAspectRatio(TextManager mgr) {
        return content.getAspectRatio(mgr);
    }

    @Override
    public void fitInto(Rectangle rectangle, TextManager mgr) {
        content.fitInto(rectangle, mgr);
    }

    @Override
    public void draw(Canvas canvas) {
        content.draw(canvas);
    }
}
