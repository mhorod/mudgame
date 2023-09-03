package middleware.remote;

import core.client.ClientGameState;
import core.events.Action;
import core.events.Event;
import core.model.PlayerID;
import core.server.ServerGameState;
import lombok.extern.slf4j.Slf4j;
import middleware.clients.ServerClient;
import middleware.clients.GameClient;
import middleware.messages_to_server.*;
import middleware.model.RoomID;
import middleware.model.RoomInfo;
import middleware.model.UserID;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Slf4j
public final class RemoteServerClient implements ServerClient {
    private final UserID myUserID;
    private final RemoteNetworkClient client;

    private RemoteGameClient currentGameClient;
    private boolean coreChanged = false;
    private List<RoomInfo> roomList = List.of();
    private RoomInfo currentRoom;

    public RemoteServerClient(UserID myUserID, RemoteNetworkClient client) {
        this.myUserID = myUserID;
        this.client = client;
    }

    public boolean isActive() {
        return equals(client.getServerClient().orElse(null));
    }

    public boolean hasCoreChanged() {
        boolean status = coreChanged;
        coreChanged = false;
        return status;
    }

    public void setRoomList(List<RoomInfo> roomList) {
        this.roomList = roomList;
    }

    public void sendMessage(MessageToServer message) {
        if (!isActive())
            throw new RuntimeException("Attempting to send message using inactive ServerClient");
        client.sendMessage(message);
    }

    public void setGameState(ClientGameState state) {
        currentGameClient = new RemoteGameClient(state, this);
        coreChanged = true;
    }

    public void registerEvent(Event event) {
        Objects.requireNonNull(currentGameClient).registerEvent(event);
    }

    @Override
    public Optional<GameClient> getGameClient() {
        return Optional.ofNullable(currentGameClient);
    }

    @Override
    public List<RoomInfo> getRoomList() {
        return Collections.unmodifiableList(roomList);
    }

    @Override
    public Optional<RoomInfo> currentRoom() {
        return Optional.ofNullable(currentRoom);
    }

    @Override
    public void refreshRoomList() {
        sendMessage(new GetRoomListMessage());
    }

    @Override
    public void joinRoom(RoomID roomID, PlayerID playerID) {
        sendMessage(new JoinRoomMessage(roomID, playerID));
    }

    @Override
    public void createRoom(PlayerID myPlayerID, int playerCount) {
        sendMessage(new CreateRoomMessage(myPlayerID, playerCount));
    }

    @Override
    public void createRoom(PlayerID myPlayerID, ServerGameState state) {
        sendMessage(new CreateRoomFromStateMessage(myPlayerID, state));
    }
}
