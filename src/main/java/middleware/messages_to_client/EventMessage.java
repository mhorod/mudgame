package middleware.messages_to_client;

import core.events.Event;
import middleware.remote.RemoteClient;

public record EventMessage(Event event) implements MessageToClient {
    @Override
    public void execute(RemoteClient client) {
        client.receiveEvent(event);
    }
}
