package middleware.messages_to_client;

import core.event.Event;
import middleware.model.RoomInfo;
import middleware.model.UserID;
import mudgame.client.ClientGameState;

import java.io.Serializable;
import java.util.List;

public interface MessageToClient extends Serializable {
    void execute(MessageToClientHandler handler);

    record ErrorMessage(String errorText) implements MessageToClient {
        @Override
        public void execute(MessageToClientHandler handler) {
            handler.error(errorText);
        }
    }

    record PingToClientMessage() implements MessageToClient {
        @Override
        public void execute(MessageToClientHandler handler) {
            handler.pingToClient();
        }
    }

    record PongToClientMessage() implements MessageToClient {
        @Override
        public void execute(MessageToClientHandler handler) {
            handler.pongToClient();
        }
    }

    record RegisterEventMessage(Event event) implements MessageToClient {
        @Override
        public void execute(MessageToClientHandler handler) {
            handler.registerEvent(event);
        }
    }

    record SetCurrentRoomMessage(RoomInfo roomInfo) implements MessageToClient {
        @Override
        public void execute(MessageToClientHandler handler) {
            handler.setCurrentRoom(roomInfo);
        }
    }

    record SetGameStateMessage(ClientGameState state) implements MessageToClient {
        @Override
        public void execute(MessageToClientHandler handler) {
            handler.setGameState(state);
        }
    }

    record SetRoomListMessage(List<RoomInfo> roomList) implements MessageToClient {
        @Override
        public void execute(MessageToClientHandler handler) {
            handler.setRoomList(roomList);
        }
    }

    record SetUserIDMessage(UserID userID) implements MessageToClient {
        @Override
        public void execute(MessageToClientHandler handler) {
            handler.setUserID(userID);
        }
    }

    record KickMessage() implements MessageToClient {
        public void execute(MessageToClientHandler handler) {
            handler.kick();
        }
    }
}
