package middleware.messages_to_server;

import core.event.Action;
import core.model.PlayerID;
import middleware.model.RoomID;
import mudgame.server.ServerGameState;

import java.io.Serializable;

public interface MessageToServer extends Serializable {
    void execute(MessageToServerHandler handler);

    record LoadGameMessage(PlayerID myPlayerID, ServerGameState state) implements MessageToServer {
        @Override
        public void execute(MessageToServerHandler handler) {
            handler.loadGame(myPlayerID, state);
        }
    }

    record MakeActionMessage(Action action) implements MessageToServer {
        @Override
        public void execute(MessageToServerHandler handler) {
            handler.makeAction(action);
        }
    }

    record GetRoomListMessage() implements MessageToServer {
        @Override
        public void execute(MessageToServerHandler handler) {
            handler.getRoomList();
        }
    }

    record LeaveRoomMessage() implements MessageToServer {
        @Override
        public void execute(MessageToServerHandler handler) {
            handler.leaveRoom();
        }
    }

    record JoinRoomMessage(PlayerID myPlayerID, RoomID roomID) implements MessageToServer {
        @Override
        public void execute(MessageToServerHandler handler) {
            handler.joinRoom(myPlayerID, roomID);
        }
    }

    record DisconnectMessage() implements MessageToServer {
        @Override
        public void execute(MessageToServerHandler handler) {
            handler.disconnect();
        }
    }

    record CreateRoomMessage(PlayerID myPlayerID, int playerCount) implements MessageToServer {
        @Override
        public void execute(MessageToServerHandler handler) {
            handler.createRoom(myPlayerID, playerCount);
        }
    }

    record PingToServerMessage() implements MessageToServer {
        @Override
        public void execute(MessageToServerHandler handler) {
            handler.pingToServer();
        }
    }

    record PongToServerMessage() implements MessageToServer {
        @Override
        public void execute(MessageToServerHandler handler) {
            handler.pongToServer();
        }
    }

    record StartGameMessage() implements MessageToServer {
        @Override
        public void execute(MessageToServerHandler handler) {
            handler.startGame();
        }
    }

    record SetNameMessage(String name) implements MessageToServer {
        @Override
        public void execute(MessageToServerHandler handler) {
            handler.setName(name);
        }
    }

    record DownloadStateMessage() implements MessageToServer {
        @Override
        public void execute(MessageToServerHandler handler) {
            handler.downloadState();
        }
    }
}
