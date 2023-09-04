package middleware.model;

import core.model.PlayerID;

import java.io.Serializable;
import java.util.Map;

public record RoomInfo(RoomID roomID, Map<PlayerID, UserID> players, UserID owner,
                       boolean isRunning) implements Serializable {
}
