package middleware.messages_to_server;

import core.model.PlayerID;
import middleware.model.RoomID;
import middleware.model.UserID;
import middleware.server.GameServer;

public record JoinRoomMessage(PlayerID myPlayerID, RoomID roomID) implements MessageToServer {
    @Override
    public void execute(GameServer server, UserID senderID) {
        server.joinRoom(senderID, myPlayerID, roomID);
    }
}
