package core.fogofwar;

import core.model.PlayerID;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public final class VisionBoard {
    private final Map<PlayerID, PlayerVisionBoard> visionBoards;

    public VisionBoard(List<PlayerID> players) {
        visionBoards = players.stream()
                .collect(Collectors.toMap(p -> p, p -> new PlayerVisionBoard()));
    }
}
