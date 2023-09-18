package middleware.server;

import core.model.PlayerID;
import lombok.extern.slf4j.Slf4j;
import middleware.communication.NetworkDevice;
import middleware.messages_to_client.MessageToClient;
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
import mudgame.server.state.ServerStateSupplier;

import java.util.function.Consumer;

@Slf4j
public final class User {
    private static long nextUserID = 0;

    private final ServerStateSupplier serverStateSupplier;
    private final MessageToClientHandler sender;
    private final NetworkDevice networkDevice;
    private final GameServer server;
    private final UserID userID;

    private Room currentRoom;
    private PlayerID currentPlayerID;
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
            loadGame(myPlayerID, serverStateSupplier.get(playerCount));
        }

        @Override
        public void loadGame(PlayerID myPlayerID, ServerState state) {
            if (sendErrorIfInRoom())
                return;
            if (!state.turnManager().players().contains(myPlayerID)) {
                sendError("Invalid PlayerID");
                return;
            }
            Room room = new Room(state, server);
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
            Room room = server.getRoom(roomID);
            if (room == null) {
                sendError("This room does not exist");
                sendRoomList();
                return;
            }
            room.joinRoom(myPlayerID, User.this);
        }

        @Override
        public void leaveRoom() {
            if (sendErrorIfNotInRoom())
                return;
            currentRoom.leaveRoom(User.this);
            sendRoomList();
        }

        @Override
        public void startGame() {
            if (sendErrorIfNotInRoom())
                return;
            currentRoom.start(User.this);
        }

        @Override
        public void makeAction(Action action) {
            if (sendErrorIfNotInRoom())
                return;
            currentRoom.processAction(action, User.this);
        }

        @Override
        public void pingToServer() {
            sender.pongToClient();
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
            sender.changeName(name);
            if (currentRoom != null)
                currentRoom.sendUpdatedInfo();
        }

        @Override
        public void downloadState() {
            if (sendErrorIfNotInRoom())
                return;
            currentRoom.downloadState(User.this);
        }
    };

    private int sinceLastCheck = 0;
    private boolean last0 = false;

    public User(
            ServerStateSupplier serverStateSupplier,
            Consumer<MessageToClient> sender, NetworkDevice networkDevice, GameServer server
    ) {
        this.serverStateSupplier = serverStateSupplier;
        this.networkDevice = networkDevice;
        this.server = server;
        this.userID = new UserID(nextUserID++);

        this.sender = new MessageToClientFactory(sender.andThen(
                message -> log.debug("[TO: {}]: {}", userID, message)
        ));

        this.server.putUser(this);
        sendRoomList();
    }

    public boolean checkRemoval() {
        if (networkDevice.isClosedOrScheduledToClose())
            return true;
        if (sinceLastCheck > 0) {
            sinceLastCheck = 0;
            last0 = false;
            return false;
        }
        if (last0)
            return true;
        last0 = true;
        sender.pingToClient();
        return false;
    }

    public void kick() {
        if (currentRoom != null)
            currentRoom.leaveRoom(this);
        sender.kick();
        networkDevice.scheduleToClose();
        server.removeUser(this);
    }

    public void processMessage(MessageToServer message) {
        synchronized (server) {
            log.debug("[FROM: {}]: {}", userID, message);
            if (networkDevice.isClosedOrScheduledToClose()) {
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
        if (currentRoom != null)
            throw new RuntimeException();
        currentRoom = newRoom;
        currentPlayerID = newPlayerID;
    }

    public void clearRoom() {
        if (currentRoom == null)
            throw new RuntimeException();
        currentRoom = null;
        currentPlayerID = null;
    }

    public void registerEvent(Event event) {
        sender.registerEvent(event);
    }

    public void setGameState(ClientGameState state) {
        sender.setGameState(state);
    }

    public void setDownloadedState(ServerState state) {
        sender.setDownloadedState(state);
    }

    public UserID getUserID() {
        return userID;
    }

    public Room getRoom() {
        return currentRoom;
    }

    public String getName() {
        return name;
    }

    public PlayerID getPlayerID() {
        return currentPlayerID;
    }

    public void sendError(String errorText) {
        sender.error(errorText);
    }

    public boolean sendErrorIfInRoom() {
        if (currentRoom != null) {
            sendError("You are already in a room");
            sendCurrentRoom();
            return true;
        }
        return false;
    }

    public boolean sendErrorIfNotInRoom() {
        if (currentRoom == null) {
            sendError("You are not in any room");
            sendCurrentRoom();
            return true;
        }
        return false;
    }

    public void sendCurrentRoom() {
        sender.setCurrentRoom(currentRoom == null ? null : currentRoom.getRoomInfo());
    }

    public void sendRoomList() {
        sender.setRoomList(server.getRoomList());
    }
}
