package core.claiming;

import core.model.PlayerID;
import core.model.Position;

import java.util.List;
import java.util.Optional;

public interface ClaimedAreaView {
    record ClaimedPosition(Position position, PlayerID owner) { }
    Optional<PlayerID> owner(Position position);
    List<ClaimedPosition> claimedPositions();
}
