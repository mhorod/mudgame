package io.menu.views.room_view;

import core.model.PlayerID;
import io.menu.Rectangle;
import io.menu.UIComponent;
import io.menu.buttons.Button;
import io.menu.buttons.ButtonSmall;
import io.menu.components.SignedImage;
import io.menu.containers.HBox;
import io.model.ScreenPosition;
import io.model.engine.Canvas;
import io.model.engine.Color;
import io.model.engine.TextManager;
import io.model.textures.Texture;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.IntStream;

public class PlayersView implements UIComponent {
    private HBox content;
    private List<? extends Button> buttons;

    public PlayersView(Map<PlayerID, String> players) {
        setPlayers(players);
    }

    public void setPlayers(Map<PlayerID, String> players) {
        buttons = IntStream.range(0, players.size())
                .mapToObj(PlayerID::new)
                .map(id -> {
                    if (players.get(id) != null) {
                        var button = new ButtonSmall(new SignedImage(
                                Texture.BASE,
                                Color.fromPlayerId(id),
                                players.get(id)
                        ));
                        button.setPressed(true);
                        return button;
                    } else
                        return new ButtonSmall(new SignedImage(
                                Texture.BASE,
                                Color.fromPlayerId(id),
                                ""
                        ));
                }).toList();
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
