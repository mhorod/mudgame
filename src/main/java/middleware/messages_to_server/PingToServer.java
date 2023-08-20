package middleware.messages_to_server;

import middleware.SimpleServer;
import middleware.UserID;
import middleware.messages_to_client.PingToClient;

public record PingToServer(String pingText, Boolean requireResponse) implements MessageToServer {
    @Override
    public void execute(SimpleServer server, UserID senderID) {
        if (requireResponse)
            server.sendMessage(senderID, new PingToClient(pingText, false));
    }
}
