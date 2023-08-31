package middleware.messages_to_client;

import core.client.ClientCore;
import core.events.Event;
import middleware.Client;

public record EventMessage(Event event) implements MessageToClient {
    @Override
    public void execute(Client client) {
        ClientCore core = client.getCore();
        if (core != null)
            core.receive(event);
    }
}
