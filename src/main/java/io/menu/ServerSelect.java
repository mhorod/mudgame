package io.menu;

import io.model.engine.Canvas;
import io.model.engine.TextManager;
import io.model.engine.TextureBank;
import io.model.input.Input;
import io.model.input.events.Click;
import io.model.input.events.EventHandler;
import io.model.input.events.Scroll;
import io.views.SimpleView;
import middleware.clients.NetworkClient;
import middleware.communication.SocketConnectionBuilder;
import middleware.remote_clients.RemoteNetworkClient;

import java.util.List;

public class ServerSelect extends SimpleView implements EventHandler {
    private final NetworkClient client = RemoteNetworkClient.GLOBAL_CLIENT;

    ButtonBlock buttons;

    public ServerSelect() {
        buttons = new ButtonBlock(
                0.1f,
                List.of(
                        new Label("LOCALHOST"),
                        new Label("AZURE"),
                        new Label("GO BACK")
                ),
                List.of(
                        () -> client.connect(new SocketConnectionBuilder("localhost", 6789)),
                        () -> client.connect(new SocketConnectionBuilder("13.69.185.38", 6789)),
                        () -> changeView(new MainMenu())
                )
        );
    }

    @Override
    public void draw(Canvas canvas) {
        buttons.draw(canvas);
    }

    @Override
    public void update(Input input, TextureBank bank, TextManager mgr) {

    }

    @Override
    public void onClick(Click click) {

    }

    @Override
    public void onScroll(Scroll scroll) {

    }
}
