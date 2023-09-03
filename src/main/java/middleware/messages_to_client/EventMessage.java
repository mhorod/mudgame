package middleware.messages_to_client;

import core.events.Event;
import middleware.remote.RemoteNetworkClient;

public record EventMessage(Event event) implements MessageToClient {
    @Override
    public void execute(RemoteNetworkClient client) {
        client.registerEvent(event);
    }
}
