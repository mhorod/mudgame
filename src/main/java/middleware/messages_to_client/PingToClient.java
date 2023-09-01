package middleware.messages_to_client;

import middleware.Client;
import middleware.messages_to_server.PingToServer;

public record PingToClient(String pingText, Boolean requireResponse) implements MessageToClient {
    @Override
    public void execute(Client client) {
        if (requireResponse)
            client.sendMessage(new PingToServer(pingText, false));
    }
}
