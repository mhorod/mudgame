package middleware.messages_to_server;

import core.event.Action;
import core.model.PlayerID;
import middleware.model.RoomID;
import mudgame.server.ServerGameState;

public interface MessageToServerHandler {
    void createRoom(PlayerID myPlayerID, int playerCount);

    void loadGame(PlayerID myPlayerID, ServerGameState state);

    void getRoomList();

    void joinRoom(PlayerID myPlayerID, RoomID roomID);

    void leaveRoom();

    void startGame();

    void makeAction(Action action);

    void pingToServer();

    void pongToServer();

    void disconnect();
}