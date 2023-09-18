package middleware.local;

import middleware.clients.AbstractGameClient;
import mudgame.client.ClientGameState;
import mudgame.controls.actions.Action;

public final class LocalClient extends AbstractGameClient {
    private final LocalServer server;

    public LocalClient(ClientGameState state, LocalServer server) {
        super(state);
        this.server = server;
    }

    @Override
    protected void sendAction(Action action) {
        server.processAction(action, myPlayerID());
    }
}
