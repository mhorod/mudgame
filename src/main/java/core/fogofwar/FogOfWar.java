package core.fogofwar;

import core.model.PlayerID;
import core.model.Position;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@EqualsAndHashCode
public class FogOfWar implements FogOfWarView, Serializable {
    private final Map<PlayerID, PlayerFogOfWar> fows;

    public FogOfWar(List<PlayerID> players) {
        fows = players.stream().collect(Collectors.toMap(p -> p, p -> new PlayerFogOfWar()));
    }

    @Override
    public boolean isVisible(Position position, PlayerID viewer) {
        return fows.get(viewer).isVisible(position);
    }

    public void setVisibility(Position position, PlayerID viewer, boolean isVisible) {
        fows.get(viewer).setVisibility(position, isVisible);
    }
}
