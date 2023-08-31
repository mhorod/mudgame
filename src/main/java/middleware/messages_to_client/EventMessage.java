package middleware.messages_to_client;

import core.events.Event;
import middleware.Client;

public record EventMessage(Event event) implements MessageToClient {
    @Override
    public void execute(Client client) {
        client.receiveEvent(event);
    }
}
