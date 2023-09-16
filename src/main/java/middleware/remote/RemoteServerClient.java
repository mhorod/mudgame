package middleware.remote;

import core.event.Action;
import core.event.Event;
import core.model.PlayerID;
import lombok.extern.slf4j.Slf4j;
import middleware.clients.GameClient;
import middleware.clients.ServerClient;
import middleware.messages_to_server.MessageToServerFactory;
import middleware.messages_to_server.MessageToServerHandler;
import middleware.model.RoomID;
import middleware.model.RoomInfo;
import middleware.model.UserID;
import mudgame.client.ClientGameState;
import mudgame.server.ServerGameState;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Slf4j
public final class RemoteServerClient implements ServerClient {
    private final RemoteNetworkClient client;

    private List<RoomInfo> roomList = List.of();
    private Optional<RoomInfo> currentRoom = Optional.empty();
    private String name = UserID.DEFAULT_NAME;

    private Optional<ServerGameState> downloadedState = Optional.empty();
    private Optional<RemoteGameClient> currentGameClient = Optional.empty();
    private boolean coreChanged = false;

    public RemoteServerClient(RemoteNetworkClient client) {
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
        if (!isActive()) {
            log.warn("Attempting to send message using inactive ServerClient");
            return new MessageToServerFactory(message -> { });
        }
        return client.getServerHandler();
    }

    public void setGameState(ClientGameState state) {
        currentGameClient = Optional.of(new RemoteGameClient(state, this));
        coreChanged = true;
    }

    public void setCurrentRoom(RoomInfo roomInfo) {
        currentRoom = Optional.ofNullable(roomInfo);
    }

    public void registerEvent(Event event) {
        currentGameClient.orElseThrow().registerEvent(event);
    }

    @Override
    public Optional<GameClient> getGameClient() {
        return currentGameClient.map(GameClient.class::cast);
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
        return currentRoom;
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

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        getServerHandler().setName(name);
    }

    public void changeName(String nameFromServer) {
        name = nameFromServer;
    }

    @Override
    public void downloadState() {
        getServerHandler().downloadState();
    }

    @Override
    public Optional<ServerGameState> getDownloadedState() {
        Optional<ServerGameState> state = downloadedState;
        downloadedState = Optional.empty();
        return state;
    }

    public void setDownloadedState(ServerGameState state) {
        downloadedState = Optional.of(state);
    }

    public void makeAction(Action action) {
        getServerHandler().makeAction(action);
    }
}
