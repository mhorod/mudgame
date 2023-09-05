package middleware.messages_to_server;

import middleware.model.UserID;
import middleware.server.GameServer;

public record StartGameMessage() implements MessageToServer {
    @Override
    public void execute(GameServer server, UserID senderID) {
        server.startGame(senderID);
    }
}
