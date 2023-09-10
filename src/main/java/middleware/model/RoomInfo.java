package middleware.model;

import core.model.PlayerID;

import java.io.Serializable;
import java.util.Map;

public record RoomInfo(RoomID roomID, Map<PlayerID, String> players, String owner,
                       boolean isRunning) implements Serializable {
}
