package core.fogofwar;

import core.model.PlayerID;
import core.model.Position;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public final class FogOfWar implements Serializable {
    private final Map<PlayerID, PlayerFogOfWar> fows;

    public FogOfWar(List<PlayerID> players) {
        fows = players.stream().collect(Collectors.toMap(p -> p, PlayerFogOfWar::new));
    }

    public boolean isVisible(Position position, PlayerID viewer) {
        return fows.get(viewer).isVisible(position);
    }

    public Set<PlayerID> players() {
        return fows.keySet();
    }

    public PlayerFogOfWar playerFogOfWar(PlayerID id) {
        return fows.get(id);
    }
}
