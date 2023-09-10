package core.claiming;

import core.fogofwar.PlayerFogOfWar;
import core.model.PlayerID;
import core.model.Position;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;

public interface ClaimedAreaView {
    record ClaimedPosition(Position position, PlayerID owner) implements Serializable { }
    Optional<PlayerID> owner(Position position);
    List<ClaimedPosition> claimedPositions();
    record ClaimChange(List<ClaimedPosition> claimedPositions,
                       List<Position> unclaimedPositions) implements Serializable {
        public static ClaimChange empty() {
            return new ClaimChange(List.of(), List.of());
        }

        public ClaimChange applyFogOfWar(PlayerFogOfWar fow) {
            return new ClaimChange(
                    claimedPositions.stream().filter(p -> fow.isVisible(p.position)).toList(),
                    unclaimedPositions.stream().toList().stream().filter(fow::isVisible).toList()
            );
        }
    }
}
