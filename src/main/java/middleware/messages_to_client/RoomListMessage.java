package middleware.messages_to_client;

import middleware.model.RoomInfo;
import middleware.remote.RemoteNetworkClient;

import java.util.List;

public record RoomListMessage(List<RoomInfo> roomList) implements MessageToClient {
    @Override
    public void execute(RemoteNetworkClient client) {
        client.setRoomList(roomList);
    }
}
