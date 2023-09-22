package core.claiming;

import core.claiming.ClaimedAreaView.ClaimedPosition;
import core.fogofwar.PlayerFogOfWarView;
import core.model.Position;
import core.terrain.TerrainView;

import java.io.Serializable;
import java.util.List;

public record ClaimChange(List<ClaimedPosition> claimedPositions,
                          List<Position> unclaimedPositions) implements Serializable {
    public static ClaimChange empty() {
        return new ClaimChange(List.of(), List.of());
    }

    public ClaimChange masked(TerrainView terrain) {
        return new ClaimChange(
                claimedPositions.stream()
                        .filter(p -> terrain.contains(p.position()))
                        .toList(),
                unclaimedPositions.stream()
                        .filter(terrain::contains)
                        .toList()
        );
    }

    public ClaimChange masked(PlayerFogOfWarView fow, TerrainView terrain) {
        return new ClaimChange(
                claimedPositions.stream()
                        .filter(p -> terrain.contains(p.position()))
                        .filter(p -> fow.isVisible(p.position()))
                        .toList(),
                unclaimedPositions.stream()
                        .filter(terrain::contains)
                        .filter(fow::isVisible)
                        .toList()
        );
    }

    public boolean isEmpty() {
        return claimedPositions.isEmpty() && unclaimedPositions.isEmpty();
    }
}
