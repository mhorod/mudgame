package middleware.server;

import lombok.extern.slf4j.Slf4j;
import middleware.model.RoomID;
import middleware.model.RoomInfo;
import middleware.model.UserID;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
public final class GameServer {
    private final Map<UserID, User> userMap = new HashMap<>();
    private final Map<RoomID, Room> roomMap = new HashMap<>();

    public synchronized void checkRemoval() {
        userMap.values().stream()
                .toList().stream()
                .filter(User::checkRemoval)
                .forEach(User::kick);
    }

    public List<RoomInfo> getRoomList() {
        return roomMap.values().stream().map(Room::getRoomInfo).toList();
    }

    public void putUser(User user) {
        UserID userID = user.getUserID();
        if (userMap.containsKey(userID))
            throw new IllegalArgumentException(userID + " already exists");
        userMap.put(userID, user);
    }

    public void removeUser(User user) {
        UserID userID = user.getUserID();
        if (!userMap.containsKey(userID))
            throw new IllegalArgumentException(userID + " does not exist");
        userMap.remove(userID);
    }

    public void putRoom(Room room) {
        RoomID roomID = room.getRoomID();
        if (roomMap.containsKey(roomID))
            throw new IllegalArgumentException(roomID + " already exists");
        roomMap.put(roomID, room);
    }

    public void removeRoom(Room room) {
        RoomID roomID = room.getRoomID();
        if (!roomMap.containsKey(roomID))
            throw new IllegalArgumentException(roomID + " does not exist");
        roomMap.remove(roomID);
    }

    public Room getRoom(RoomID roomID) {
        return roomMap.get(roomID);
    }
}
