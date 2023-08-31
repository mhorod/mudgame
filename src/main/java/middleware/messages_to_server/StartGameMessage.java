package middleware.messages_to_server;

import middleware.GameServer;
import middleware.UserID;

public record StartGameMessage() implements MessageToServer {
    @Override
    public void execute(GameServer server, UserID senderID) {
        throw new UnsupportedOperationException();
    }
}
