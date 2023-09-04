package middleware.messages_to_client;

import middleware.remote.RemoteNetworkClient;
import mudgame.events.Event;

public record EventMessage(Event event) implements MessageToClient {
    @Override
    public void execute(RemoteNetworkClient client) {
        client.registerEvent(event);
    }
}
