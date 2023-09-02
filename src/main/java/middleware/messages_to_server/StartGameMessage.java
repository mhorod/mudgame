package middleware.messages_to_server;

import middleware.remote.GameServer;
import middleware.remote.UserID;

public record StartGameMessage() implements MessageToServer {
    @Override
    public void execute(GameServer server, UserID senderID) {
        throw new UnsupportedOperationException();
    }
}
