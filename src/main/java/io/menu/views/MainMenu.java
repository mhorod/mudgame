package io.menu.views;

import io.menu.Rectangle;
import io.menu.components.ButtonBlock;
import io.menu.components.Label;
import io.model.ScreenPosition;
import io.model.engine.Canvas;
import io.model.engine.StateManager;
import io.model.engine.TextManager;
import io.model.engine.TextureBank;
import io.model.input.Input;
import io.model.input.events.Click;
import io.model.input.events.EventHandler;
import io.model.input.events.Scroll;
import io.model.textures.Texture;
import io.model.textures.TextureDrawData;
import io.views.SimpleView;
import lombok.extern.slf4j.Slf4j;
import middleware.clients.NetworkClient;
import middleware.communication.NetworkStatus;
import middleware.communication.SocketConnectionBuilder;
import middleware.remote_clients.RemoteNetworkClient;

import java.util.List;

@Slf4j
public class MainMenu extends SimpleView implements EventHandler {
    private final NetworkClient client = RemoteNetworkClient.GLOBAL_CLIENT;

    ButtonBlock buttons;
    Rectangle logo = new Rectangle(Texture.LOGO.aspectRatio());

    public MainMenu() {
        client.disconnect();
        buttons = new ButtonBlock(
                0.1f,
                List.of(
                        new Label("LOCAL"),
                        new Label("LOCALHOST"),
                        new Label("REMOTE"),
                        new Label("EXIT")
                ),
                List.of(
//                        () -> changeView(new RoomSelect()),
                        () -> {
                        },
                        () -> client.connect(new SocketConnectionBuilder("localhost", 6789)),
                        () -> client.connect(new SocketConnectionBuilder("13.69.185.38", 6789)),
                        () -> System.exit(0)
                )
        );
    }

    @Override
    public void draw(Canvas canvas) {
        buttons.draw(canvas);
        canvas.draw(new TextureDrawData(
                Texture.LOGO,
                logo.position,
                logo.height
        ));
    }

    @Override
    public void update(Input input, TextureBank bank, TextManager mgr, StateManager stateManager) {
        if (client.getNetworkStatus() == NetworkStatus.OK) {
            changeView(new RoomSelect(client.getServerClient().orElseThrow()));
            return;
        }

        var window = new Rectangle(input.window().height() / input.window().width());
        window.position = new ScreenPosition(0, 0);
        window.height = input.window().height() / input.window().width();

        var scene = new Rectangle(0.5f);
        scene.fitInto(window);

        input.events().forEach(event -> event.accept(this));
        buttons.fitInto(new Rectangle(scene.position.x(), scene.position.y(), scene.width() / 2,
                scene.height), mgr);
        buttons.update(input.mouse().position(), input.mouse().leftPressed());
        logo.fitInto(new Rectangle(scene.position.x() + scene.width() / 2, scene.position.y(),
                scene.width() / 2, scene.height));
    }

    @Override
    public void onClick(Click click) {
        buttons.click(click.position());
    }

    @Override
    public void onScroll(Scroll scroll) {

    }
}
