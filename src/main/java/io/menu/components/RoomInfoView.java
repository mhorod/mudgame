package io.menu.components;

import io.menu.Image;
import io.menu.Rectangle;
import io.menu.UIComponent;
import io.menu.containers.HBox;
import io.model.engine.Canvas;
import io.model.engine.Color;
import io.model.engine.TextManager;
import io.model.textures.Texture;
import middleware.model.RoomInfo;

public class RoomInfoView implements UIComponent {

    private final HBox players;
    private final Image fire = new Image(Texture.FIRE);
    private final RoomInfo info;

    public RoomInfoView(RoomInfo info) {
        this.info = info;
        players = new HBox(0.1f, info.players().entrySet().stream().map(entry -> {
            if (entry.getValue() == null)
                return new SignedImage(Texture.BASE, "");
            else
                return new SignedImage(Texture.BASE, Color.fromPlayerId(entry.getKey()), entry.getValue());
        }).toList());
    }


    @Override
    public float getAspectRatio(TextManager mgr) {
        return players.getAspectRatio(mgr);
    }

    @Override
    public void fitInto(Rectangle rectangle, TextManager mgr) {
        players.fitInto(new Rectangle(
                rectangle.position.x(),
                rectangle.position.y(),
                rectangle.width() * 0.8f,
                rectangle.height
        ), mgr);
        fire.fitInto(new Rectangle(
                rectangle.position.x() + rectangle.width() * 0.8f,
                rectangle.position.y() + rectangle.height * 0.1f,
                rectangle.width() * 0.2f,
                rectangle.height * 0.8f
        ), mgr);
    }

    @Override
    public void draw(Canvas canvas) {
        players.draw(canvas);
        if (info.isRunning())
            fire.draw(canvas);
    }
}
