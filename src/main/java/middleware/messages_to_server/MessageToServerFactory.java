package middleware.messages_to_server;

import mudgame.controls.actions.Action;
import core.model.PlayerID;
import lombok.AllArgsConstructor;
import middleware.model.RoomID;
import mudgame.server.state.ServerState;

import java.util.function.Consumer;

import static middleware.messages_to_server.MessageToServer.*;

@AllArgsConstructor
public final class MessageToServerFactory implements MessageToServerHandler {
    private final Consumer<MessageToServer> consumer;

    @Override
    public void createRoom(PlayerID myPlayerID, int playerCount) {
        consumer.accept(new CreateRoomMessage(myPlayerID, playerCount));
    }

    @Override
    public void loadGame(PlayerID myPlayerID, ServerState state) {
        consumer.accept(new LoadGameMessage(myPlayerID, state));
    }

    @Override
    public void getRoomList() {
        consumer.accept(new GetRoomListMessage());
    }

    @Override
    public void joinRoom(PlayerID myPlayerID, RoomID roomID) {
        consumer.accept(new JoinRoomMessage(myPlayerID, roomID));
    }

    @Override
    public void leaveRoom() {
        consumer.accept(new LeaveRoomMessage());
    }

    @Override
    public void startGame() {
        consumer.accept(new StartGameMessage());
    }

    @Override
    public void makeAction(Action action) {
        consumer.accept(new MakeActionMessage(action));
    }

    @Override
    public void pingToServer() {
        consumer.accept(new PingToServerMessage());
    }

    @Override
    public void pongToServer() {
        consumer.accept(new PongToServerMessage());
    }

    @Override
    public void disconnect() {
        consumer.accept(new DisconnectMessage());
    }

    @Override
    public void setName(String name) {
        consumer.accept(new SetNameMessage(name));
    }

    @Override
    public void downloadState() {
        consumer.accept(new DownloadStateMessage());
    }
}
