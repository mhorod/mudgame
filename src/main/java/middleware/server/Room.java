package middleware.server;

import core.model.PlayerID;
import middleware.model.RoomID;
import middleware.model.RoomInfo;
import mudgame.client.ClientGameState;
import mudgame.controls.actions.Action;
import mudgame.server.EventOccurrence;
import mudgame.server.MudServerCore;
import mudgame.server.state.ServerState;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public final class Room {
    private static long nextRoomID = 0;

    private final GameServer server;
    private final RoomID roomID;
    private final MudServerCore core;

    private final Map<PlayerID, Optional<User>> toUserMap = new LinkedHashMap<>();
    private final Set<PlayerID> validPlayerIDs = toUserMap.keySet();

    private final Set<User> connectedUsers = new LinkedHashSet<>();

    private Optional<User> owner = Optional.empty();
    private boolean isRunning = false;

    public Room(ServerState state, GameServer server) {
        this.server = server;
        this.roomID = new RoomID(nextRoomID++);
        this.core = new MudServerCore(state, this::eventObserver);

        server.putRoom(this);
        for (PlayerID playerID : state.turnManager().players())
            toUserMap.put(playerID, Optional.empty());
    }

    private void eventObserver(EventOccurrence eventOccurrence) {
        eventOccurrence
                .recipients()
                .stream()
                .map(toUserMap::get)
                .flatMap(Optional::stream)
                .forEach(user -> user.registerEvent(eventOccurrence.event()));
    }

    public void sendUpdatedInfo() {
        connectedUsers.forEach(User::sendCurrentRoom);
    }

    public void sendGameState(User user) {
        if (!isRunning)
            return;
        ClientGameState state = core.clientState(user.getPlayerID());
        user.setGameState(state);
    }

    public boolean joinRoom(PlayerID asPlayerID, User user) {
        if (!validPlayerIDs.contains(asPlayerID)) {
            user.sendError("This PlayedID is not valid");
            return false;
        }
        if (toUserMap.get(asPlayerID).isPresent()) {
            user.sendError("This PlayedID is already taken");
            return false;
        }

        if (owner.isEmpty())
            owner = Optional.of(user);

        user.setRoom(this, asPlayerID);
        toUserMap.put(asPlayerID, Optional.of(user));
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
        toUserMap.put(playerID, Optional.empty());
        connectedUsers.remove(user);

        if (user.equals(owner.orElse(null)))
            owner = connectedUsers.stream().findFirst();

        sendUpdatedInfo();
        checkRemoval();
    }

    public RoomID getRoomID() {
        return roomID;
    }

    public RoomInfo getRoomInfo() {
        Map<PlayerID, String> toUserIDMap = new LinkedHashMap<>();
        for (var entry : toUserMap.entrySet()) {
            PlayerID playerID = entry.getKey();
            String username = entry.getValue().map(User::getName).orElse(null);
            toUserIDMap.put(playerID, username);
        }

        return new RoomInfo(roomID, toUserIDMap, owner.map(User::getName).orElse(null), isRunning);
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
        if (!user.equals(owner.orElse(null))) {
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

    public void downloadState(User user) {
        if (sendErrorIfNotOwner(user))
            return;
        user.setDownloadedState(core.state());
    }

    public void processAction(Action action, User actor) {
        if (sendErrorIfNotStarted(actor))
            return;
        core.process(action, actor.getPlayerID());
    }
}
