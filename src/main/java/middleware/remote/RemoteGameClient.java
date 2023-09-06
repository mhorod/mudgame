package middleware.remote;

import core.event.Action;
import middleware.clients.AbstractGameClient;
import middleware.messages_to_server.ActionMessage;
import mudgame.client.ClientGameState;

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
    public void sendAction(Action action) {
        if (!isActive())
            throw new RuntimeException("Attempting to send action using inactive GameClient");
        client.sendMessage(new ActionMessage(action));
    }
}