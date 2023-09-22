package io.menu.views.room_view;

import io.game.GameView;
import io.menu.Rectangle;
import io.menu.components.Image;
import io.menu.views.RoomSelect;
import io.model.engine.*;
import io.model.input.Input;
import io.model.input.events.Click;
import io.model.input.events.EventHandler;
import io.model.input.events.Scroll;
import io.model.textures.Texture;
import io.views.SimpleView;
import middleware.clients.ServerClient;
import middleware.model.RoomID;

import java.util.stream.Stream;

public class RoomView extends SimpleView implements EventHandler {
    private final ServerClient client;
    private final RoomID roomID;
    private final RoomViewCommon common;
    Image selectedColor;

    public RoomView(ServerClient client, RoomID room) {
        this.client = client;
        this.roomID = room;
        common = new RoomViewCommon(client, room, this::changeView);
    }

    @Override
    public void draw(Canvas canvas) {
        common.draw(canvas);
        selectedColor.draw(canvas);
    }

    @Override
    public void update(Input input, TextureBank bank, TextManager mgr, StateManager stateManager) {
        if (client.isOwner()) {
            changeView(new OwnerRoomView(client, roomID));
            return;
        }
        var maybeRoom = Stream.concat(
                client.getRoomList().stream().filter(info -> info.roomID().equals(roomID)),
                client.currentRoom().stream()
        ).findAny();
        if (maybeRoom.isEmpty()) {
            changeView(new RoomSelect(client));
            return;
        }
        var room = maybeRoom.get();

        var maybeGameClient = client.getGameClient();
        if (maybeGameClient.isPresent()) {
            changeView(new GameView(client));
            return;
        }

        selectedColor = new Image(Texture.BASE, client.myPlayerID().map(Color::fromPlayerId).orElse(Color.WHITE));

        var window = new Rectangle(0, 0, 1, input.window().height() / input.window().width());
        common.fitInto(window, mgr);

        selectedColor.fitInto(new Rectangle(
                window.position.x(),
                window.position.y() + 0.4f * window.height,
                window.width(),
                window.height * 0.6f
        ), mgr);

        input.events().forEach(event -> event.accept(this));
        common.update(input.mouse().position(), input.mouse().leftPressed(), room);
    }

    @Override
    public void onClick(Click click) {
        common.click(click);
    }

    @Override
    public void onScroll(Scroll scroll) {
    }
}
