package middleware.remote;

import core.event.Action;
import lombok.extern.slf4j.Slf4j;
import middleware.clients.AbstractGameClient;
import mudgame.client.ClientGameState;

@Slf4j
public final class RemoteGameClient extends AbstractGameClient {
    private final RemoteServerClient client;

    public RemoteGameClient(ClientGameState state, RemoteServerClient client) {
        super(state);
        this.client = client;
    }

    public boolean isActive() {
        return equals(client.getGameClient().orElse(null));
    }

    @Override
    protected void sendAction(Action action) {
        if (!isActive()) {
            log.warn("Attempting to send action using inactive GameClient");
            return;
        }
        client.makeAction(action);
    }
}
