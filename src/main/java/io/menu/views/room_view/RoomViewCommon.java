package io.menu.views.room_view;

import io.menu.Rectangle;
import io.menu.UIComponent;
import io.menu.buttons.Button;
import io.menu.buttons.ButtonSmall;
import io.menu.components.Label;
import io.menu.views.RoomSelect;
import io.model.ScreenPosition;
import io.model.engine.Canvas;
import io.model.engine.TextManager;
import io.model.input.events.Click;
import io.views.SimpleView;
import middleware.clients.ServerClient;
import middleware.model.RoomID;
import middleware.model.RoomInfo;

import java.util.Map;
import java.util.function.Consumer;

public class RoomViewCommon implements UIComponent {
    private final ServerClient client;
    private final RoomID roomID;
    PlayersView playersView = new PlayersView(Map.of());
    Button goBack = new ButtonSmall(new Label("BACK"));
    private final Consumer<SimpleView> changeView;


    public RoomViewCommon(ServerClient client, RoomID room, Consumer<SimpleView> changeView) {
        this.client = client;
        this.roomID = room;
        this.changeView = changeView;
    }

    @Override
    public void draw(Canvas canvas) {
        playersView.draw(canvas);
        goBack.draw(canvas);
    }

    public void update(ScreenPosition mouse, boolean pressed, RoomInfo room) {
        playersView.setPlayers(room.players());
        playersView.update(mouse, pressed);
        goBack.update(mouse, pressed);
    }

    public void click(Click click) {
        playersView.click(click.position(), playerID -> {
            if (client != null)
                client.joinRoom(roomID, playerID);
        });
        if (goBack.contains(click.position())) {
            client.leaveCurrentRoom();
            changeView.accept(new RoomSelect(client));
        }
    }

    @Override
    public float getAspectRatio(TextManager mgr) {
        return 1;
    }

    @Override
    public void fitInto(Rectangle rectangle, TextManager mgr) {
        goBack.fitInto(new Rectangle(
                rectangle.position.x(),
                rectangle.position.y() + rectangle.height - rectangle.width() * 0.1f,
                rectangle.width() * 0.1f,
                rectangle.width() * 0.1f
        ), mgr);
        playersView.fitInto(new Rectangle(
                rectangle.position.x(),
                rectangle.position.y(),
                rectangle.width(),
                rectangle.height * 0.3f
        ), mgr);

    }
}
