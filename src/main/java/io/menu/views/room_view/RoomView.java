package io.menu.views.room_view;

import io.game.GameView;
import io.menu.Image;
import io.menu.Rectangle;
import io.menu.RoomSelect;
import io.model.ScreenPosition;
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

import java.util.Map;
import java.util.stream.Stream;

public class RoomView extends SimpleView implements EventHandler {
    private final ServerClient client;
    private final RoomID roomID;
    Image selectedColor;

    PlayersView playersView = new PlayersView(Map.of());


    public RoomView(ServerClient client, RoomID room) {
        this.client = client;
        this.roomID = room;
    }

    @Override
    public void draw(Canvas canvas) {
        playersView.draw(canvas);
        selectedColor.draw(canvas);
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
        playersView.setPlayers(room.players());
        selectedColor = new Image(Texture.BASE, client.myPlayerID().map(Color::fromPlayerId).orElse(Color.WHITE));

        var maybeGameClient = client.getGameClient();
        if (maybeGameClient.isPresent()) {
            changeView(new GameView(maybeGameClient.get()));
            return;
        }

        var window = new Rectangle(input.window().height() / input.window().width());
        window.position = new ScreenPosition(0, 0);
        window.height = input.window().height() / input.window().width();

        var scene = new Rectangle(
                0.025f,
                0.025f,
                window.width() - 0.05f,
                window.height - 0.05f
        );

        playersView.fitInto(new Rectangle(
                scene.position.x(),
                scene.position.y(),
                scene.width(),
                scene.height * 0.3f
        ), mgr);
        selectedColor.fitInto(new Rectangle(
                scene.position.x(),
                scene.position.y() + scene.height * 0.4f,
                scene.width(),
                scene.height * 0.6f
        ), mgr);
        input.events().forEach(event -> event.accept(this));
        playersView.update(input.mouse().position(), input.mouse().leftPressed());
    }

    @Override
    public void onClick(Click click) {
        playersView.click(click.position(), playerID -> {
            if (client != null)
                client.joinRoom(roomID, playerID);
        });
    }

    @Override
    public void onScroll(Scroll scroll) {
    }
}
