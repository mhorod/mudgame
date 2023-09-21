package io.menu;

import io.animation.Easer;
import io.game.GameView;
import io.menu.components.RoomInfoView;
import io.menu.scroll.ScrollBox;
import io.menu.views.create_room.CreateRoom;
import io.menu.views.room_view.RoomView;
import io.model.ScreenPosition;
import io.model.engine.Canvas;
import io.model.engine.TextManager;
import io.model.engine.TextureBank;
import io.model.input.Input;
import io.model.input.events.Click;
import io.model.input.events.EventHandler;
import io.model.input.events.Scroll;
import io.model.textures.Texture;
import io.views.SimpleView;
import middleware.clients.ServerClient;
import middleware.model.RoomInfo;

import java.util.ArrayList;
import java.util.List;

public class RoomSelect extends SimpleView implements EventHandler {
    private final ServerClient client;

    ButtonBlock buttons;
    ScrollBox scrollBox;

    Easer scrollEaser;

    Rectangle logo = new Rectangle(Texture.LOGO.aspectRatio());

    public RoomSelect(ServerClient client) {
        this.client = client;
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
    }

    @Override
    public void update(Input input, TextureBank bank, TextManager mgr) {
        var maybeGameClient = client.getGameClient();
        if (maybeGameClient.isPresent()) {
            changeView(new GameView(maybeGameClient.get()));
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
        scrollBox = new ScrollBox(buttons);

        var window = new Rectangle(input.window().height() / input.window().width());
        window.position = new ScreenPosition(0, 0);
        window.height = input.window().height() / input.window().width();

        var scene = new Rectangle(
                0.025f,
                0.025f,
                window.width() - 0.025f,
                window.height - 0.05f
        );

        scrollEaser.update(input.deltaTime());
        scrollBox.fitInto(scene, mgr);
        buttons.update(input.mouse().position(), input.mouse().leftPressed());
        logo.fitInto(new Rectangle(scene.position.x() + scene.width() / 2, scene.position.y(),
                scene.width() / 2, scene.height));
        input.events().forEach(event -> event.accept(this));
    }

    @Override
    public void onClick(Click click) {
        buttons.click(click.position());
    }

    @Override
    public void onScroll(Scroll scroll) {
        scrollEaser.setTarget(
                Math.min(Math.max(scrollEaser.getTarget() + scroll.amount() * 0.1f, 0),
                        scrollBox.getMaxScroll()));
    }
}
