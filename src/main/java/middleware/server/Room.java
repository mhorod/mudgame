package middleware.server;

import core.event.Action;
import core.event.EventOccurrence;
import core.model.PlayerID;
import middleware.model.RoomID;
import middleware.model.RoomInfo;
import middleware.model.UserID;
import mudgame.client.ClientGameState;
import mudgame.server.MudServerCore;
import mudgame.server.ServerGameState;

import java.util.*;

public final class Room {
    private static long nextRoomID = 0;

    private final GameServer server;
    private final RoomID roomID;
    private final MudServerCore core;

    private final Map<PlayerID, User> toUserMap = new LinkedHashMap<>();
    private final Set<PlayerID> validPlayerIDs = toUserMap.keySet();

    private final Set<User> connectedUsers = new LinkedHashSet<>();

    private User owner;
    private boolean isRunning = false;

    public Room(ServerGameState state, GameServer server) {
        this.server = server;
        this.roomID = new RoomID(nextRoomID++);
        this.core = new MudServerCore(state, this::eventObserver);

        server.putRoom(this);
        for (PlayerID playerID : state.playerManager().getPlayerIDs())
            toUserMap.put(playerID, null);
    }

    private void eventObserver(EventOccurrence eventOccurrence) {
        eventOccurrence
                .recipients()
                .stream()
                .map(toUserMap::get)
                .filter(Objects::nonNull)
                .forEach(user -> user.registerEvent(eventOccurrence.event()));
    }

    private void sendUpdatedInfo() {
        connectedUsers.forEach(User::sendCurrentRoom);
    }

    public void sendGameState(User user) {
        if (!isRunning)
            return;
        ClientGameState state = core.state().toClientGameState(user.getPlayerID());
        user.setGameState(state);
    }

    public boolean joinRoom(PlayerID asPlayerID, User user) {
        if (!validPlayerIDs.contains(asPlayerID)) {
            user.sendError("This PlayedID is not valid");
            return false;
        }
        if (toUserMap.get(asPlayerID) != null) {
            user.sendError("This PlayedID is already taken");
            return false;
        }

        if (owner == null)
            owner = user;

        user.setRoom(this, asPlayerID);
        toUserMap.put(asPlayerID, user);
        connectedUsers.add(user);

        sendUpdatedInfo();
        if (isRunning)
            sendGameState(user);
        return true;
    }

    public void checkRemoval() {
        if (connectedUsers.isEmpty())
            server.removeRoom(this);
    }

    public void leaveRoom(User user) {
        PlayerID playerID = user.getPlayerID();

        user.clearRoom();
        toUserMap.put(playerID, null);
        connectedUsers.remove(user);

        if (user.equals(owner) && !connectedUsers.isEmpty())
            owner = connectedUsers.iterator().next();

        sendUpdatedInfo();
        checkRemoval();
    }

    public UserID getOwnerID() {
        return owner == null ? null : owner.getUserID();
    }

    public RoomID getRoomID() {
        return roomID;
    }

    public RoomInfo getRoomInfo() {
        Map<PlayerID, UserID> toUserIDMap = new LinkedHashMap<>();
        for (var entry : toUserMap.entrySet()) {
            PlayerID playerID = entry.getKey();
            User user = entry.getValue();
            toUserIDMap.put(playerID, user == null ? null : user.getUserID());
        }
        return new RoomInfo(roomID, toUserIDMap, getOwnerID(), isRunning);
    }

    public void start(User actor) {
        if (sendErrorIfStarted(actor))
            return;
        if (sendErrorIfNotOwner(actor))
            return;

        isRunning = true;
        for (User user : connectedUsers)
            sendGameState(user);
        sendUpdatedInfo();
    }

    private boolean sendErrorIfNotOwner(User user) {
        if (!user.equals(owner)) {
            user.sendError("You are not owner of this room");
            return true;
        }
        return false;
    }

    private boolean sendErrorIfNotStarted(User user) {
        if (!isRunning) {
            user.sendError("Game is not started yet");
            return true;
        }
        return false;
    }

    private boolean sendErrorIfStarted(User user) {
        if (isRunning) {
            user.sendError("Game is already started");
            return true;
        }
        return false;
    }

    public void processAction(Action action, User actor) {
        if (sendErrorIfNotStarted(actor))
            return;
        core.process(action, actor.getPlayerID());
    }
}
