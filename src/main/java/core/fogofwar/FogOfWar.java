package core.fogofwar;

import core.model.PlayerID;
import core.model.Position;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@EqualsAndHashCode
public final class FogOfWar implements Serializable {
    private final Map<PlayerID, PlayerFogOfWar> fows;

    public FogOfWar(List<PlayerID> players) {
        fows = players.stream().collect(Collectors.toMap(p -> p, p -> new PlayerFogOfWar()));
    }

    public boolean isVisible(Position position, PlayerID viewer) {
        return fows.get(viewer).isVisible(position);
    }
}
