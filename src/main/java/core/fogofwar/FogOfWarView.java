package core.fogofwar;

import core.model.PlayerID;
import core.model.Position;

public interface FogOfWarView {
    boolean isVisible(Position position, PlayerID viewer);

    PlayerFogOfWarView playerView(PlayerID viewer);
}
