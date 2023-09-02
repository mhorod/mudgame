package middleware.local;

import core.client.ClientGameState;
import core.events.Action;
import middleware.AbstractGameClient;

public final class LocalClient extends AbstractGameClient {
    private final LocalServer server;

    public LocalClient(ClientGameState state, LocalServer server) {
        super(state);
        this.server = server;
    }

    @Override
    public void sendAction(Action action) {
        server.processAction(action, myPlayerID());
    }
}
