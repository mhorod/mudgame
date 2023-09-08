package core.fogofwar;

import core.model.PlayerID;
import core.model.Position;

import java.util.Set;

public interface FogOfWarView {
    boolean isVisible(Position position, PlayerID viewer);
    Set<PlayerID> players();
    PlayerFogOfWarView playerFogOfWarView(PlayerID id);
}
