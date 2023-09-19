package middleware.server;

import lombok.extern.slf4j.Slf4j;
import middleware.communication.NetworkDeviceBuilder;
import middleware.model.RoomID;
import middleware.model.RoomInfo;
import middleware.model.UserID;
import mudgame.server.state.ServerState;
import mudgame.server.state.ServerStateSupplier;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
public final class GameServer {
    private final ServerStateSupplier stateSupplier;

    private final Map<UserID, User> userMap = new LinkedHashMap<>();
    private final Map<RoomID, Room> roomMap = new LinkedHashMap<>();

    private long nextUserID = 0;
    private long nextRoomID = 0;

    public GameServer(ServerStateSupplier stateSupplier) {
        this.stateSupplier = stateSupplier;
    }

    public synchronized void checkRemoval() {
        userMap.values().stream()
                .toList().stream()
                .filter(User::checkRemoval)
                .forEach(User::kick);
    }

    public synchronized void stop() {
        userMap.values().stream().toList().forEach(User::kick);
    }

    public List<RoomInfo> getRoomInfoList() {
        return roomMap.values().stream().map(Room::getRoomInfo).toList();
    }

    public List<User> getUserList() {
        return userMap.values().stream().toList();
    }

    public User createUser(NetworkDeviceBuilder builder) {
        User user = new User(builder, new UserID(nextUserID++), this);
        userMap.put(user.getUserID(), user);
        return user;
    }

    public Room createRoom(ServerState state) {
        Room room = new Room(state, new RoomID(nextRoomID++), this);
        roomMap.put(room.getRoomID(), room);
        return room;
    }

    public void removeUser(User user) {
        UserID userID = user.getUserID();
        if (!userMap.containsKey(userID))
            throw new IllegalArgumentException(userID + " does not exist");
        userMap.remove(userID);
    }

    public void removeRoom(Room room) {
        RoomID roomID = room.getRoomID();
        if (!roomMap.containsKey(roomID))
            throw new IllegalArgumentException(roomID + " does not exist");
        roomMap.remove(roomID);
    }

    public Optional<Room> getRoom(RoomID roomID) {
        return Optional.ofNullable(roomMap.get(roomID));
    }

    public ServerStateSupplier getStateSupplier() {
        return stateSupplier;
    }
}
