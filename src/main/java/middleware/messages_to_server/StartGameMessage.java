package middleware.messages_to_server;

import middleware.server.GameServer;
import middleware.model.UserID;

public record StartGameMessage() implements MessageToServer {
    @Override
    public void execute(GameServer server, UserID senderID) {
        throw new UnsupportedOperationException();
    }
}
