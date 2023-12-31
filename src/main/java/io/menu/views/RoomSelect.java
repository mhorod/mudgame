package io.menu.views;

import io.animation.Easer;
import io.game.GameView;
import io.menu.Rectangle;
import io.menu.UIComponent;
import io.menu.buttons.Button;
import io.menu.buttons.ButtonSmall;
import io.menu.components.ButtonBlock;
import io.menu.components.Label;
import io.menu.components.RoomInfoView;
import io.menu.scroll.ScrollBox;
import io.menu.views.create_room.CreateRoom;
import io.menu.views.room_view.RoomView;
import io.model.engine.Canvas;
import io.model.engine.StateManager;
import io.model.engine.TextManager;
import io.model.engine.TextureBank;
import io.model.input.Input;
import io.model.input.events.Click;
import io.model.input.events.EventHandler;
import io.model.input.events.Scroll;
import io.views.SimpleView;
import middleware.clients.ServerClient;
import middleware.model.RoomInfo;

import java.util.ArrayList;
import java.util.List;

public class RoomSelect extends SimpleView implements EventHandler {
    private final ServerClient client;

    ButtonBlock buttons;
    ScrollBox scrollBox;

    Button goBack = new ButtonSmall(new Label("BACK"));

    Easer scrollEaser;

    public RoomSelect(ServerClient client) {
        this.client = client;
        this.scrollBox = new ScrollBox(new Label("loading"));
        scrollEaser = new Easer(0) {
            @Override
            public void onUpdate(float value) {
                scrollBox.setScroll(value);
            }
        };
    }

    @Override
    public void draw(Canvas canvas) {
        scrollBox.draw(canvas);
        goBack.draw(canvas);
    }

    @Override
    public void update(Input input, TextureBank bank, TextManager mgr, StateManager stateManager) {
        var maybeGameClient = client.getGameClient();
        if (maybeGameClient.isPresent()) {
            changeView(new GameView(client));
            return;
        }

        List<UIComponent> contents = new ArrayList<>();
        List<Runnable> handlers = new ArrayList<>();

        contents.add(new Label("CREATE"));
        handlers.add(() -> changeView(new CreateRoom(client)));

        for (RoomInfo info : client.getRoomList()) {
            contents.add(new RoomInfoView(info));
            handlers.add(() -> changeView(new RoomView(client, info.roomID())));
        }

        buttons = new ButtonBlock(0.01f, contents, handlers);
        scrollBox.setContents(buttons);

        var window = new Rectangle(0.025f, 0.025f, 0.95f, input.window().height() / input.window().width() - 0.05f);

        var scene = new Rectangle(
                window.position.x() + 0.2f,
                window.position.y(),
                window.width() - 0.225f,
                window.height
        );

        goBack.fitInto(new Rectangle(
                window.position.x(),
                window.position.y() + window.height - 0.1f * window.width(),
                window.width() * 0.1f,
                window.width() * 0.1f
        ), mgr);

        scrollBox.fitInto(scene, mgr);
        scrollEaser.update(input.deltaTime());
        buttons.update(input.mouse().position(), input.mouse().leftPressed());
        goBack.update(input.mouse().position(), input.mouse().leftPressed());
        input.events().forEach(event -> event.accept(this));
    }

    @Override
    public void onClick(Click click) {
        buttons.click(click.position());
        if (goBack.contains(click.position())) {
            changeView(new MainMenu());
        }
    }

    @Override
    public void onScroll(Scroll scroll) {
        scrollEaser.setTarget(
                Math.min(Math.max(scrollEaser.getTarget() + scroll.amount() * 0.1f, 0),
                        scrollBox.getMaxScroll()));
    }
}
