package middleware.messages_to_server;

import core.model.PlayerID;
import middleware.model.UserID;
import middleware.server.GameServer;

public record CreateRoomMessage(PlayerID myPlayerID, int playerCount) implements MessageToServer {
    @Override
    public void execute(GameServer server, UserID senderID) {
        server.createRoom(senderID, myPlayerID, playerCount);
    }
}
