package middleware.messages_to_client;

import middleware.messages_to_server.PingToServer;
import middleware.remote.RemoteNetworkClient;

public record PingToClient(String pingText, Boolean requireResponse) implements MessageToClient {
    @Override
    public void execute(RemoteNetworkClient client) {
        if (requireResponse)
            client.sendMessage(new PingToServer(pingText, false));
    }
}
