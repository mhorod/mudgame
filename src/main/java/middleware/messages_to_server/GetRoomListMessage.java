package middleware.messages_to_server;

import middleware.model.UserID;
import middleware.server.GameServer;

public record GetRoomListMessage() implements MessageToServer {
    @Override
    public void execute(GameServer server, UserID senderID) {
        server.sendRoomList(senderID);
    }
}
