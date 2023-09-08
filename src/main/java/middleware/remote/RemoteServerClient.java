package middleware.remote;

import core.event.Action;
import core.event.Event;
import core.model.PlayerID;
import middleware.clients.GameClient;
import middleware.clients.ServerClient;
import middleware.messages_to_server.MessageToServerHandler;
import middleware.model.RoomID;
import middleware.model.RoomInfo;
import middleware.model.UserID;
import mudgame.client.ClientGameState;
import mudgame.server.ServerGameState;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public final class RemoteServerClient implements ServerClient {
    private final UserID myUserID;
    private final RemoteNetworkClient client;

    private List<RoomInfo> roomList = List.of();
    private RoomInfo currentRoom;

    private boolean coreChanged = false;
    private RemoteGameClient currentGameClient;

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

    private MessageToServerHandler getServerHandler() {
        if (!isActive())
            throw new RuntimeException("Attempting to send message using inactive ServerClient");
        return client.getSender();
    }

    public void setGameState(ClientGameState state) {
        currentGameClient = new RemoteGameClient(state, this);
        coreChanged = true;
    }

    public void setCurrentRoom(RoomInfo roomInfo) {
        currentRoom = roomInfo;
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

    public void setRoomList(List<RoomInfo> roomList) {
        this.roomList = roomList;
    }

    @Override
    public Optional<RoomInfo> currentRoom() {
        return Optional.ofNullable(currentRoom);
    }

    @Override
    public UserID myUsedID() {
        return myUserID;
    }

    @Override
    public void leaveCurrentRoom() {
        getServerHandler().leaveRoom();
    }

    @Override
    public void refreshRoomList() {
        getServerHandler().getRoomList();
    }

    @Override
    public void joinRoom(RoomID roomID, PlayerID playerID) {
        getServerHandler().joinRoom(playerID, roomID);
    }

    @Override
    public void createRoom(PlayerID myPlayerID, int playerCount) {
        getServerHandler().createRoom(myPlayerID, playerCount);
    }

    @Override
    public void createRoom(PlayerID myPlayerID, ServerGameState state) {
        getServerHandler().loadGame(myPlayerID, state);
    }

    @Override
    public void startGame() {
        getServerHandler().startGame();
    }

    public void makeAction(Action action) {
        getServerHandler().makeAction(action);
    }
}
