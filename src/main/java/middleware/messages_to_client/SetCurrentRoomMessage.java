package middleware.messages_to_client;

import middleware.model.RoomInfo;
import middleware.remote.RemoteNetworkClient;

public record SetCurrentRoomMessage(RoomInfo roomInfo) implements MessageToClient {
    @Override
    public void execute(RemoteNetworkClient client) {
        client.setCurrentRoom(roomInfo);
    }
}
