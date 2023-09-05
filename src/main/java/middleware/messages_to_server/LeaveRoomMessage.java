package middleware.messages_to_server;

import middleware.model.UserID;
import middleware.server.GameServer;

public record LeaveRoomMessage() implements MessageToServer {
    @Override
    public void execute(GameServer server, UserID senderID) {
        server.leaveRoom(senderID);
    }
}
