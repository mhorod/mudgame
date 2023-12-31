package middleware.server;

import core.model.PlayerID;
import lombok.extern.slf4j.Slf4j;
import middleware.communication.NetworkDevice;
import middleware.communication.NetworkDeviceBuilder;
import middleware.messages_to_client.MessageToClientFactory;
import middleware.messages_to_client.MessageToClientHandler;
import middleware.messages_to_server.MessageToServer;
import middleware.messages_to_server.MessageToServerHandler;
import middleware.model.RoomID;
import middleware.model.UserID;
import mudgame.client.ClientGameState;
import mudgame.controls.actions.Action;
import mudgame.controls.events.Event;
import mudgame.server.state.ServerState;

import java.util.Optional;

@Slf4j
public final class User {
    private final NetworkDevice networkDevice;
    private final GameServer server;
    private final UserID userID;

    private Optional<Room> currentRoom = Optional.empty();
    private Optional<PlayerID> currentPlayerID = Optional.empty();
    private String name = UserID.DEFAULT_NAME;

    private final MessageToServerHandler messageToServerHandler = new MessageToServerHandler() {
        @Override
        public void createRoom(PlayerID myPlayerID, int playerCount) {
            if (sendErrorIfInRoom())
                return;
            // TODO check playerCount <= MAX_PC
            if (playerCount <= 0) {
                sendError("playerCount must be positive");
                return;
            }
            loadGame(myPlayerID, server.getStateSupplier().get(playerCount));
        }

        @Override
        public void loadGame(PlayerID myPlayerID, ServerState state) {
            if (sendErrorIfInRoom())
                return;
            if (!state.turnManager().players().contains(myPlayerID)) {
                sendError("Invalid PlayerID");
                return;
            }
            Room room = server.createRoom(state);
            room.joinRoom(myPlayerID, User.this);
            room.checkRemoval();
        }

        @Override
        public void getRoomList() {
            sendRoomList();
        }

        @Override
        public void joinRoom(PlayerID myPlayerID, RoomID roomID) {
            if (sendErrorIfInRoom())
                return;
            Optional<Room> room = server.getRoom(roomID);
            if (room.isEmpty()) {
                sendError("This room does not exist");
                sendRoomList();
                return;
            }
            room.orElseThrow().joinRoom(myPlayerID, User.this);
        }

        @Override
        public void leaveRoom() {
            if (sendErrorIfNotInRoom())
                return;
            currentRoom.orElseThrow().leaveRoom(User.this);
            sendRoomList();
        }

        @Override
        public void startGame() {
            if (sendErrorIfNotInRoom())
                return;
            currentRoom.orElseThrow().start(User.this);
        }

        @Override
        public void makeAction(Action action) {
            if (sendErrorIfNotInRoom())
                return;
            currentRoom.orElseThrow().processAction(action, User.this);
        }

        @Override
        public void pingToServer() {
            getClientHandler().pongToClient();
        }

        @Override
        public void pongToServer() {
            // noop
        }

        @Override
        public void disconnect() {
            kick();
        }

        @Override
        public void setName(String nameFromClient) {
            name = nameFromClient;
            getClientHandler().changeName(name);
            currentRoom.ifPresent(Room::sendUpdatedInfo);
        }

        @Override
        public void downloadState() {
            if (sendErrorIfNotInRoom())
                return;
            currentRoom.orElseThrow().downloadState(User.this);
        }
    };

    private int sinceLastCheck = 0;
    private boolean last0 = false;

    public User(NetworkDeviceBuilder builder, UserID userID, GameServer server) {
        this.networkDevice = builder.build(this::processMessage, MessageToServer.class)
                .orElseThrow();
        this.userID = userID;
        this.server = server;

        sendError("You got: " + userID);
        sendRoomList();
    }

    public MessageToClientHandler getClientHandler() {
        return new MessageToClientFactory(message -> {
            log.info("[TO: {}]: {}", userID, message);
            networkDevice.send(message);
        });
    }

    public boolean checkRemoval() {
        if (networkDevice.isClosed())
            return true;
        if (sinceLastCheck > 0) {
            sinceLastCheck = 0;
            last0 = false;
            return false;
        }
        if (last0)
            return true;
        last0 = true;
        getClientHandler().pingToClient();
        return false;
    }

    public void kick() {
        currentRoom.ifPresent(room -> room.leaveRoom(this));
        getClientHandler().kick();
        networkDevice.close();
        server.removeUser(this);
    }

    public void processMessage(MessageToServer message) {
        synchronized (server) {
            log.info("[FROM: {}]: {}", userID, message);
            if (networkDevice.isClosed()) {
                kick();
                return;
            }
            sinceLastCheck++;
            try {
                message.execute(messageToServerHandler);
            } catch (RuntimeException exc) {
                log.error("Exception happened while processing a message: ", exc);
                sendError("Exception happened while processing your message");
                kick();
            }
        }
    }

    public void setRoom(Room newRoom, PlayerID newPlayerID) {
        if (currentRoom.isPresent())
            throw new RuntimeException();
        currentRoom = Optional.of(newRoom);
        currentPlayerID = Optional.of(newPlayerID);
    }

    public void clearRoom() {
        if (currentRoom.isEmpty())
            throw new RuntimeException();
        currentRoom = Optional.empty();
        currentPlayerID = Optional.empty();
    }

    public void registerEvent(Event event) {
        getClientHandler().registerEvent(event);
    }

    public void setGameState(ClientGameState state) {
        getClientHandler().setGameState(state);
    }

    public void setDownloadedState(ServerState state) {
        getClientHandler().setDownloadedState(state);
    }

    public UserID getUserID() {
        return userID;
    }

    public String getName() {
        return name;
    }

    public Optional<Room> getRoom() {
        return currentRoom;
    }

    public Optional<PlayerID> getPlayerID() {
        return currentPlayerID;
    }

    public void sendError(String errorText) {
        getClientHandler().error(errorText);
    }

    public boolean sendErrorIfInRoom() {
        if (currentRoom.isPresent()) {
            sendError("You are already in a room");
            sendCurrentRoom();
            return true;
        }
        return false;
    }

    public boolean sendErrorIfNotInRoom() {
        if (currentRoom.isEmpty()) {
            sendError("You are not in any room");
            sendCurrentRoom();
            return true;
        }
        return false;
    }

    public void sendCurrentRoom() {
        if (currentRoom.isEmpty()) {
            getClientHandler().setCurrentRoom(null, false, null);
            return;
        }
        Room room = currentRoom.orElseThrow();
        getClientHandler().setCurrentRoom(
                room.getRoomInfo(),
                equals(room.getOwner().orElseThrow()),
                currentPlayerID.orElseThrow()
        );
    }

    public void sendRoomList() {
        getClientHandler().setRoomList(server.getRoomInfoList());
    }
}
