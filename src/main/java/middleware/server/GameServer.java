package middleware.server;

import core.events.Action;
import core.model.PlayerID;
import core.server.ServerCore;
import core.server.ServerGameState;
import lombok.extern.slf4j.Slf4j;
import middleware.communication.Sender;
import middleware.messages_to_client.*;
import middleware.messages_to_server.MessageToServer;
import middleware.model.RoomID;
import middleware.model.RoomInfo;
import middleware.model.UserID;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
public final class GameServer {
    private final Map<UserID, Sender<MessageToClient>> senderMap = new HashMap<>();
    private final Map<UserID, RoomID> userToRoomMap = new HashMap<>();
    private final Map<RoomID, Room> roomMap = new HashMap<>();

    public synchronized void processMessage(UserID source, MessageToServer message) {
        log.info("[from: " + source + "]: " + message);
        message.execute(this, source);
    }

    public void addConnection(UserID userID, Sender<MessageToClient> sender) {
        if (senderMap.containsKey(userID))
            throw new IllegalArgumentException(userID + " is already connected");
        senderMap.put(userID, sender);
        sendMessage(userID, new SetUserIDMessage(userID));
    }

    public void removeConnection(UserID userID) {
        if (!senderMap.containsKey(userID))
            throw new IllegalArgumentException(userID + " is not connected");
        senderMap.remove(userID);
        if (userToRoomMap.containsKey(userID))
            leaveRoom(userID);
    }

    public void joinRoom(UserID userID, PlayerID asPlayerID, RoomID roomID) {
        Room room = roomMap.get(roomID);
        if (room == null) {
            sendError(userID, "This room does not exist");
            return;
        }

        room.joinRoom(userID, asPlayerID);
    }

    public void startGame(UserID userID) {
        RoomID roomID = userToRoomMap.get(userID);
        if (roomID == null)
            sendError(userID, "You are not in any room");
        roomMap.get(roomID).start(userID);
    }

    public void leaveRoom(UserID userID) {
        RoomID roomID = userToRoomMap.get(userID);
        if (roomID == null) {
            sendError(userID, "You are not in any room");
            return;
        }
        roomMap.get(roomID).leaveRoom(userID);
    }

    public void createRoom(UserID userID, PlayerID asPlayerID, ServerGameState state) {
        Room room = new Room(state, this);
        room.joinRoom(userID, asPlayerID);
        room.checkDeletion();
    }

    public void createRoom(UserID userID, PlayerID asPlayerID, int playerCount) {
        if (playerCount <= 0) {
            sendError(userID, "playerCount should be positive");
            return;
        }

        // TODO fix this + check if playerCount is valid correctly
        ServerGameState state = new ServerCore(playerCount).state();
        createRoom(userID, asPlayerID, state);
    }

    public void sendMessage(UserID destination, MessageToClient message) {
        Sender<MessageToClient> sender = senderMap.get(destination);
        log.info("[to: " + destination + ", sender is working: " + (sender != null) + "]: " + message);
        if (sender != null)
            sender.sendMessage(message);
    }

    public void sendError(UserID destination, String errorText) {
        sendMessage(destination, new ErrorMessage(errorText));
    }

    public List<RoomInfo> getRoomList() {
        return roomMap.values().stream().map(Room::getRoomInfo).toList();
    }

    public void sendRoomList(UserID userID) {
        sendMessage(userID, new RoomListMessage(getRoomList()));
    }

    public void sendCurrentRoomInfo(UserID userID) {
        RoomID roomID = userToRoomMap.get(userID);
        RoomInfo roomInfo = roomID == null ? null : roomMap.get(roomID).getRoomInfo();
        sendMessage(userID, new SetCurrentRoomMessage(roomInfo));
    }

    public void processAction(Action action, UserID actor) {
        RoomID roomID = userToRoomMap.get(actor);
        if (roomID == null)
            sendError(actor, "You are not in any room");
        roomMap.get(roomID).processAction(action, actor);
    }

    /* These methods are called only from Room class */

    RoomID getRoomOfUser(UserID userID) {
        return userToRoomMap.get(userID);
    }

    void setRoomOfUser(UserID userID, RoomID roomID) {
        userToRoomMap.put(userID, roomID);
    }

    void clearRoomOfUser(UserID userID) {
        userToRoomMap.remove(userID);
    }

    void removeRoom(RoomID roomID) {
        if (!roomMap.containsKey(roomID))
            throw new IllegalArgumentException(roomID + " does not exist");
        roomMap.remove(roomID);
    }

    void addRoom(Room room) {
        RoomID roomID = room.getRoomID();
        if (roomMap.containsKey(roomID))
            throw new IllegalArgumentException(roomID + " already exists");
        roomMap.put(roomID, room);
    }
}
