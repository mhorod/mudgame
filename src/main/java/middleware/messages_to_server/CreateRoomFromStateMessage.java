package middleware.messages_to_server;

import core.model.PlayerID;
import core.server.ServerGameState;
import middleware.model.UserID;
import middleware.server.GameServer;

public record CreateRoomFromStateMessage(PlayerID myPlayerID, ServerGameState state) implements MessageToServer {
    @Override
    public void execute(GameServer server, UserID senderID) {
        server.createRoom(senderID, myPlayerID, state);
    }
}
