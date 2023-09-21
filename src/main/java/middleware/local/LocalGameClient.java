package middleware.local;

import middleware.clients.AbstractGameClient;
import mudgame.client.ClientGameState;
import mudgame.controls.actions.Action;

public final class LocalGameClient extends AbstractGameClient {
    private final LocalServer server;

    public LocalGameClient(ClientGameState state, LocalServer server) {
        super(state);
        this.server = server;
    }

    @Override
    protected void sendAction(Action action) {
        server.processAction(action, myPlayerID());
    }
}
