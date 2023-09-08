package middleware.messages_to_client;

import core.event.Event;
import middleware.model.RoomInfo;
import middleware.model.UserID;
import mudgame.client.ClientGameState;

import java.util.List;

public interface MessageToClientHandler {
    void error(String errorText);

    void pingToClient();

    void pongToClient();

    void registerEvent(Event event);

    void setCurrentRoom(RoomInfo roomInfo);

    void setGameState(ClientGameState state);

    void setRoomList(List<RoomInfo> roomList);

    void setUserID(UserID userID);

    void kick();
}
