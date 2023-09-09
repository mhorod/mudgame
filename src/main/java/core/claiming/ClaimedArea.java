package core.claiming;

import core.fogofwar.PlayerFogOfWar;
import core.model.PlayerID;
import core.model.Position;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;

import static java.util.stream.Collectors.toMap;

public class ClaimedArea implements ClaimedAreaView {
    private final Map<Position, PlayerID> claimed;

    public ClaimedArea() {
        claimed = new HashMap<>();
    }

    private ClaimedArea(Map<Position, PlayerID> claimed) {
        this.claimed = claimed;
    }


    public ClaimedArea applyFogOfWar(PlayerFogOfWar fow) {
        Map<Position, PlayerID> maskedClaimed = claimed
                .entrySet()
                .stream()
                .filter(e -> fow.isVisible(e.getKey()))
                .collect(toMap(Entry::getKey, Entry::getValue));
        return new ClaimedArea(maskedClaimed);
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

    public void claim(PlayerID player, List<Position> positions) {
        for (Position position : positions)
            claimed.put(position, player);
    }

    public void unclaim(List<Position> positions) {
        for (Position position : positions)
            claimed.remove(position);
    }
}
