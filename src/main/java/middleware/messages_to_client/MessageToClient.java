package middleware.messages_to_client;

import middleware.model.RoomInfo;
import mudgame.client.ClientGameState;
import mudgame.controls.events.Event;
import mudgame.server.state.ServerState;

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

    record KickMessage() implements MessageToClient {
        public void execute(MessageToClientHandler handler) {
            handler.kick();
        }
    }

    record ChangeNameMessage(String name) implements MessageToClient {
        public void execute(MessageToClientHandler handler) {
            handler.changeName(name);
        }
    }

    record SetDownloadedStateMessage(ServerState state) implements MessageToClient {
        public void execute(MessageToClientHandler handler) {
            handler.setDownloadedState(state);
        }
    }
}
