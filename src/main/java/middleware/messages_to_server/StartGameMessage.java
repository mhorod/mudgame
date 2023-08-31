package middleware.messages_to_server;

import middleware.SimpleServer;
import middleware.UserID;

public record StartGameMessage() implements MessageToServer {
    @Override
    public void execute(SimpleServer server, UserID senderID) {
        throw new UnsupportedOperationException();
    }
}
