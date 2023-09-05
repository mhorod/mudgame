package middleware.messages_to_client;

import core.event.Event;
import lombok.AllArgsConstructor;
import middleware.model.RoomInfo;
import middleware.model.UserID;
import mudgame.client.ClientGameState;

import java.util.List;
import java.util.function.Consumer;

import static middleware.messages_to_client.MessageToClient.*;

@AllArgsConstructor
public final class MessageToClientFactory implements MessageToClientHandler {
    private final Consumer<MessageToClient> consumer;

    @Override
    public void error(String errorText) {
        consumer.accept(new ErrorMessage(errorText));
    }

    @Override
    public void pingToClient() {
        consumer.accept(new PingToClientMessage());
    }

    @Override
    public void pongToClient() {
        consumer.accept(new PongToClientMessage());
    }

    @Override
    public void registerEvent(Event event) {
        consumer.accept(new RegisterEventMessage(event));
    }

    @Override
    public void setCurrentRoom(RoomInfo roomInfo) {
        consumer.accept(new SetCurrentRoomMessage(roomInfo));
    }

    @Override
    public void setGameState(ClientGameState state) {
        consumer.accept(new SetGameStateMessage(state));
    }

    @Override
    public void setRoomList(List<RoomInfo> roomList) {
        consumer.accept(new SetRoomListMessage(roomList));
    }

    @Override
    public void setUserID(UserID userID) {
        consumer.accept(new SetUserIDMessage(userID));
    }

    @Override
    public void kick() {
        consumer.accept(new KickMessage());
    }
}
