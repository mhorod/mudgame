package middleware.messages_to_client;

import middleware.messages_to_server.PingToServer;
import middleware.remote.RemoteClient;

public record PingToClient(String pingText, Boolean requireResponse) implements MessageToClient {
    @Override
    public void execute(RemoteClient client) {
        if (requireResponse)
            client.sendMessage(new PingToServer(pingText, false));
    }
}
