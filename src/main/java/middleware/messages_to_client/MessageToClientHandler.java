package middleware.messages_to_client;

import mudgame.controls.events.Event;
import middleware.model.RoomInfo;
import mudgame.client.ClientGameState;
import mudgame.server.ServerGameState;

import java.util.List;

public interface MessageToClientHandler {
    void error(String errorText);

    void pingToClient();

    void pongToClient();

    void registerEvent(Event event);

    void setCurrentRoom(RoomInfo roomInfo);

    void setGameState(ClientGameState state);

    void setRoomList(List<RoomInfo> roomList);

    void kick();

    void changeName(String name);

    void setDownloadedState(ServerGameState state);
}
