package core.claiming;

import core.model.PlayerID;
import core.model.Position;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class PlayerClaimedArea implements ClaimedAreaView {
    private final Map<Position, PlayerID> claimed;

    PlayerClaimedArea() { this.claimed = new HashMap<>(); }

    PlayerClaimedArea(Map<Position, PlayerID> claimed) {
        this.claimed = new HashMap<>(claimed);
    }

    @Override
    public Optional<PlayerID> owner(Position position) {
        if (claimed.containsKey(position))
            return Optional.of(claimed.get(position));
        else
            return Optional.empty();
    }

    @Override
    public List<ClaimedPosition> claimedPositions() {
        return claimed.entrySet()
                .stream()
                .map(e -> new ClaimedPosition(e.getKey(), e.getValue()))
                .toList();
    }

    public void apply(ClaimChange claimChange) {
        for (Position p : claimChange.unclaimedPositions())
            claimed.remove(p);
        for (ClaimedPosition p : claimChange.claimedPositions())
            claimed.put(p.position(), p.owner());
    }

    public void claim(PlayerID player, List<Position> positions) {
        for (Position position : positions)
            claimed.put(position, player);
    }

    public void unclaim(List<Position> positions) {
        for (Position position : positions)
            claimed.remove(position);
    }
}
