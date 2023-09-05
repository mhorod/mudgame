package middleware.server;

import core.event.Action;
import core.event.EventOccurrence;
import core.model.PlayerID;
import middleware.messages_to_client.EventMessage;
import middleware.messages_to_client.MessageToClient;
import middleware.messages_to_client.SetGameStateMessage;
import middleware.model.RoomID;
import middleware.model.RoomInfo;
import middleware.model.UserID;
import mudgame.client.ClientGameState;
import mudgame.server.MudServerCore;
import mudgame.server.ServerGameState;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

public final class Room {
    private static long nextRoomID = 0;

    private final GameServer server;
    private final RoomID roomID;
    private final MudServerCore core;

    private final Map<UserID, PlayerID> toPlayerIDMap = new LinkedHashMap<>();
    private final Map<PlayerID, UserID> toUserIDMap = new HashMap<>();

    private final Set<UserID> connectedUserIDs = toPlayerIDMap.keySet();
    private final Set<PlayerID> validPlayerIDs = toUserIDMap.keySet();

    private UserID ownerID;
    private boolean isRunning = false;

    public Room(ServerGameState state, GameServer server) {
        this.server = server;
        this.roomID = new RoomID(nextRoomID++);
        this.core = new MudServerCore(state, this::eventObserver);

        server.addRoom(this);
        for (PlayerID playerID : state.playerManager().getPlayerIDs())
            toUserIDMap.put(playerID, null);
    }

    private void eventObserver(EventOccurrence eventOccurrence) {
        for (PlayerID playerID : eventOccurrence.recipients())
            sendMessageToPlayer(playerID, new EventMessage(eventOccurrence.event()));
    }

    private void sendMessageToPlayer(PlayerID destination, MessageToClient message) {
        if (!validPlayerIDs.contains(destination))
            throw new IllegalArgumentException(
                    destination + " is not valid PlayerID for this room");
        UserID userID = toUserIDMap.get(destination);
        if (userID != null)
            server.sendMessage(userID, message);
    }

    private void sendUpdatedInfo() {
        for (UserID userID : connectedUserIDs)
            server.sendCurrentRoomInfo(userID);
    }

    public void checkDeletion() {
        if (connectedUserIDs.size() == 0)
            server.removeRoom(roomID);
    }

    public void sendClientGameState(UserID userID) {
        if (!isRunning)
            throw new RuntimeException("This room is not stated");
        if (!connectedUserIDs.contains(userID))
            throw new IllegalArgumentException("This user is not part of this game");

        PlayerID playerID = toPlayerIDMap.get(userID);
        ClientGameState state = core.state().toClientGameState(playerID);

        server.sendMessage(userID, new SetGameStateMessage(state));
    }

    public void sendServerGameState(UserID userID) {
        if (ownerID != userID) {
            server.sendError(userID, "You are not owner of this room");
            return;
        }

        throw new UnsupportedOperationException();
    }

    public boolean joinRoom(UserID userID, PlayerID asPlayerID) {
        if (server.getRoomOfUser(userID) != null) {
            server.sendError(userID, "You are already connected to some room");
            return false;
        }
        if (!validPlayerIDs.contains(asPlayerID)) {
            server.sendError(userID, "This PlayedID is not valid");
            return false;
        }
        if (toUserIDMap.get(asPlayerID) != null) {
            server.sendError(userID, "This PlayedID is already taken");
            return false;
        }

        if (ownerID == null)
            ownerID = userID;
        toPlayerIDMap.put(userID, asPlayerID);
        toUserIDMap.put(asPlayerID, userID);

        server.setRoomOfUser(userID, roomID);
        sendUpdatedInfo();
        if (isRunning)
            sendClientGameState(userID);
        return true;
    }

    public void leaveRoom(UserID userID) {
        if (!connectedUserIDs.contains(userID))
            throw new IllegalArgumentException(userID + " is not in this room");
        PlayerID playerID = toPlayerIDMap.get(userID);
        connectedUserIDs.remove(userID);
        toUserIDMap.put(playerID, null);

        if (userID == ownerID && !connectedUserIDs.isEmpty())
            ownerID = connectedUserIDs.iterator().next();

        server.clearRoomOfUser(userID);
        sendUpdatedInfo();
        server.sendCurrentRoomInfo(userID);
        checkDeletion();
    }

    public RoomInfo getRoomInfo() {
        return new RoomInfo(roomID, toUserIDMap, ownerID, isRunning);
    }

    public RoomID getRoomID() {
        return roomID;
    }

    public void start(UserID actorID) {
        if (isRunning) {
            server.sendError(actorID, "You can not start game twice");
            return;
        }
        if (ownerID != actorID) {
            server.sendError(actorID, "Only room owner can start the game");
            return;
        }

        isRunning = true;
        for (UserID userID : connectedUserIDs)
            sendClientGameState(userID);
    }

    public void processAction(Action action, UserID actor) {
        if (!connectedUserIDs.contains(actor))
            throw new IllegalArgumentException("This user is not in this room");

        core.process(action, toPlayerIDMap.get(actor));
    }
}
