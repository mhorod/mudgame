package io.menu.views.room_view;

import io.game.GameView;
import io.menu.Rectangle;
import io.menu.buttons.Button;
import io.menu.buttons.ButtonMedium;
import io.menu.components.Image;
import io.menu.components.Label;
import io.menu.views.RoomSelect;
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
import middleware.clients.ServerClient;
import middleware.model.RoomID;

import java.util.Objects;
import java.util.stream.Stream;

public class OwnerRoomView extends SimpleView implements EventHandler {
    private final ServerClient client;
    private final RoomID roomID;
    Image selectedColor;

    Button start = new ButtonMedium(new Label("LET'S GO"));
    private final RoomViewCommon common;


    public OwnerRoomView(ServerClient client, RoomID room) {
        this.client = client;
        this.roomID = room;
        common = new RoomViewCommon(client, room, this::changeView);
    }

    @Override
    public void draw(Canvas canvas) {
        selectedColor.draw(canvas);
        start.draw(canvas);
        common.draw(canvas);
    }

    @Override
    public void update(Input input, TextureBank bank, TextManager mgr) {
        var maybeRoom = Stream.concat(
                client.getRoomList().stream().filter(info -> info.roomID().equals(roomID)),
                client.currentRoom().stream()
        ).findAny();
        if (maybeRoom.isEmpty()) {
            changeView(new RoomSelect(client));
            return;
        }
        var room = maybeRoom.get();
        start.setPressed(!room.players().values().stream().allMatch(Objects::nonNull));
        selectedColor = new Image(Texture.BASE, client.myPlayerID().map(Color::fromPlayerId).orElse(Color.WHITE));

        var maybeGameClient = client.getGameClient();
        if (maybeGameClient.isPresent()) {
            changeView(new GameView(maybeGameClient.get()));
            return;
        }

        var window = new Rectangle(0, 0, 1, input.window().height() / input.window().width());
        selectedColor.fitInto(new Rectangle(
                window.position.x(),
                window.position.y() + window.height * 0.4f,
                window.width() * 0.5f,
                window.height * 0.6f
        ), mgr);
        start.fitInto(new Rectangle(
                window.position.x() + window.width() * 0.6f,
                window.position.y() + window.height * 0.4f,
                window.width() * 0.3f,
                window.height * 0.6f
        ), mgr);
        common.fitInto(window, mgr);
        input.events().forEach(event -> event.accept(this));
        start.update(input.mouse().position(), input.mouse().leftPressed());
        common.update(input.mouse().position(), input.mouse().leftPressed(), room);
    }

    @Override
    public void onClick(Click click) {
        common.click(click);
        if (start.contains(click.position()))
            client.startGame();
    }

    @Override
    public void onScroll(Scroll scroll) {
    }
}
