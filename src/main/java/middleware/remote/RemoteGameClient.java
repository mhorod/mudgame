package middleware.remote;

import core.client.ClientGameState;
import core.events.Action;
import core.events.Event;
import middleware.AbstractGameClient;
import middleware.messages_to_server.ActionMessage;

import java.util.Optional;

public final class RemoteGameClient extends AbstractGameClient {
    private final RemoteClient client;

    public RemoteGameClient(ClientGameState state, RemoteClient client) {
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

    // TODO remove this hack
    @Override
    public Optional<Event> peekEvent() {
        client.processAllMessages();
        return super.peekEvent();
    }
}
