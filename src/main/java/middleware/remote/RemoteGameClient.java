package middleware.remote;

import core.client.ClientGameState;
import core.events.Action;
import middleware.AbstractGameClient;
import middleware.messages_to_server.ActionMessage;

public final class RemoteGameClient extends AbstractGameClient {
    private final RemoteClient client;

    public RemoteGameClient(RemoteClient client, ClientGameState state) {
        super(state);
        this.client = client;
    }

    public boolean isActive() {
        return equals(client.getGameClient().orElse(null));
    }

    @Override
    public void sendAction(Action action) {
        if (isActive())
            client.sendMessage(new ActionMessage(action));
        else
            throw new RuntimeException("Attempting to send Action using inactive client");
    }
}
