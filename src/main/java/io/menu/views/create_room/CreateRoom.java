package io.menu.views.create_room;

import io.game.GameView;
import io.menu.Rectangle;
import io.menu.buttons.Button;
import io.menu.buttons.ButtonSmall;
import io.menu.components.Label;
import io.menu.views.RoomSelect;
import io.menu.views.room_view.OwnerRoomView;
import io.model.ScreenPosition;
import io.model.engine.Canvas;
import io.model.engine.StateManager;
import io.model.engine.TextManager;
import io.model.engine.TextureBank;
import io.model.input.Input;
import io.model.input.events.Click;
import io.model.input.events.EventHandler;
import io.model.input.events.Scroll;
import io.views.SimpleView;
import lombok.extern.slf4j.Slf4j;
import middleware.clients.ServerClient;
import mudgame.server.state.ServerState;

import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

@Slf4j
public class CreateRoom extends SimpleView implements EventHandler {
    private final ServerClient client;

    private static final int MAX_PLAYERS = 5;
    NumberPicker numberPicker;
    ColorPicker colorPicker;
    Button loadButton;
    StateManager stateManager;

    Future<Optional<ServerState>> futureState;
    ServerState loadedState;

    Label or = new Label("OR");
    Button goBack = new ButtonSmall(new Label("BACK"));

    void loadState(ServerState state) {
        int playerCount = state.turnManager().playerCount();

        numberPicker = new NumberPicker(playerCount, playerCount);
        colorPicker = new ColorPicker(playerCount);
        loadButton = new ButtonSmall(new Label("UNLOAD"));
        loadedState = state;
    }

    void unloadState() {
        int playerCount = numberPicker != null ? numberPicker.getNumber() : 1;

        numberPicker = new NumberPicker(1, MAX_PLAYERS);
        numberPicker.setNumber(playerCount);
        colorPicker = new ColorPicker(playerCount);
        loadButton = new ButtonSmall(new Label("LOAD"));
        loadedState = null;
    }

    public CreateRoom(ServerClient client) {
        unloadState();
        this.client = client;
    }

    @Override
    public void draw(Canvas canvas) {
        numberPicker.draw(canvas);
        colorPicker.draw(canvas);
        or.draw(canvas);
        goBack.draw(canvas);
        loadButton.draw(canvas);
    }

    private boolean futureBlocks() {
        if (futureState == null)
            return false;
        if (!futureState.isDone())
            return true;
        try {
            Optional<ServerState> state = futureState.get();
            if (state.isPresent())
                loadState(state.orElseThrow());
        } catch (ExecutionException | InterruptedException e) {
            log.info("This should not happen: ", e);
        }
        futureState = null;
        return false;
    }

    @Override
    public void update(Input input, TextureBank bank, TextManager mgr, StateManager stateManager) {
        if (futureBlocks())
            return;

        this.stateManager = stateManager;

        var maybeCurrentRoom = client.currentRoom();
        if (maybeCurrentRoom.isPresent()) {
            changeView(new OwnerRoomView(client, maybeCurrentRoom.get().roomID()));
            return;
        }
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

        or.fitInto(new Rectangle(
                scene.position.x() + scene.width() / 2 - scene.height * 0.05f,
                scene.position.y() + 0.65f * scene.height,
                scene.height * 0.1f,
                scene.height * 0.1f
        ), mgr);

        goBack.fitInto(new Rectangle(
                window.position.x(),
                window.position.y() + window.height - 0.1f * window.width(),
                window.width() * 0.1f,
                window.width() * 0.1f
        ), mgr);

        numberPicker.update(input.deltaTime());
        input.events().forEach(event -> event.accept(this));
        colorPicker.fitInto(new Rectangle(
                scene.position.x(),
                scene.position.y(),
                scene.width(),
                scene.height * 0.3f
        ), mgr);
        loadButton.fitInto(new Rectangle(
                scene.position.x() + 0.7f * scene.width(),
                scene.position.y() + scene.height * 0.4f,
                scene.width() * 0.2f,
                scene.height * 0.6f
        ), mgr);
        colorPicker.update(input.mouse().position(), input.mouse().leftPressed());
        loadButton.update(input.mouse().position(), input.mouse().leftPressed());
        numberPicker.fitInto(new Rectangle(
                scene.position.x() + 0.1f * scene.width(),
                scene.position.y() + scene.height * 0.4f,
                scene.width() * 0.3f,
                scene.height * 0.6f
        ), mgr);
    }

    @Override
    public void onClick(Click click) {
        colorPicker.click(click.position(), playerID -> {
            if (client != null) {
                if (loadedState == null)
                    client.createRoom(playerID, numberPicker.getNumber());
                else
                    client.createRoom(playerID, loadedState);
            }
        });
        if (goBack.contains(click.position()))
            changeView(new RoomSelect(client));
        if (loadButton.contains(click.position())) {
            if (loadedState == null)
                futureState = stateManager.loadState();
            else
                unloadState();
        }
    }

    @Override
    public void onScroll(Scroll scroll) {
        if (loadedState != null)
            return;
        var diff = -(int) scroll.amount();
        var noPlayers = numberPicker.getNumber() + diff;
        if (noPlayers < 1) noPlayers = 1;
        if (noPlayers > MAX_PLAYERS) noPlayers = MAX_PLAYERS;
        colorPicker.setNumberOfPlayers(noPlayers);
        numberPicker.setNumber(noPlayers);
    }
}
