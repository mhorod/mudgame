package middleware.messages_to_server;

import core.model.PlayerID;
import middleware.model.RoomID;
import mudgame.controls.actions.Action;
import mudgame.server.state.ServerState;

public interface MessageToServerHandler {
    void createRoom(PlayerID myPlayerID, int playerCount);

    void loadGame(PlayerID myPlayerID, ServerState state);

    void getRoomList();

    void joinRoom(PlayerID myPlayerID, RoomID roomID);

    void leaveRoom();

    void startGame();

    void makeAction(Action action);

    void pingToServer();

    void pongToServer();

    void disconnect();

    void setName(String name);

    void downloadState();
}
