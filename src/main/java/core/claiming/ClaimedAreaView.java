package core.claiming;

import core.fogofwar.PlayerFogOfWarView;
import core.model.PlayerID;
import core.model.Position;
import core.terrain.TerrainView;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;

public interface ClaimedAreaView {
    record ClaimedPosition(Position position, PlayerID owner) implements Serializable { }
    Optional<PlayerID> owner(Position position);
    List<ClaimedPosition> claimedPositions();
}
