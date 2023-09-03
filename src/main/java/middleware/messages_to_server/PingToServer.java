package middleware.messages_to_server;

import middleware.messages_to_client.PingToClient;
import middleware.server.GameServer;
import middleware.model.UserID;

public record PingToServer(String pingText, Boolean requireResponse) implements MessageToServer {
    @Override
    public void execute(GameServer server, UserID senderID) {
        if (requireResponse)
            server.sendMessage(senderID, new PingToClient(pingText, false));
    }
}
