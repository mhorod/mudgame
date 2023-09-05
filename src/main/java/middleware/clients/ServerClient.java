package middleware.clients;

import core.model.PlayerID;
import middleware.model.RoomID;
import middleware.model.RoomInfo;
import middleware.model.UserID;
import mudgame.server.ServerGameState;

import java.util.List;
import java.util.Optional;

public interface ServerClient {
    boolean hasCoreChanged();

    Optional<GameClient> getGameClient();

    List<RoomInfo> getRoomList();

    Optional<RoomInfo> currentRoom();

    UserID myUsedID();

    void leaveCurrentRoom();

    void refreshRoomList();

    void joinRoom(RoomID roomID, PlayerID playerID);

    void createRoom(PlayerID myPlayerID, int playerCount);

    void createRoom(PlayerID myPlayerID, ServerGameState state);

    void startGame();
}
