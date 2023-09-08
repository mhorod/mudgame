package mudgame.client;

import core.entities.EntityBoard;
import core.fogofwar.PlayerFogOfWar;
import core.model.PlayerID;
import core.terrain.model.Terrain;
import core.turns.PlayerManager;
import mudgame.server.rules.ActionRule;

import java.io.Serializable;
import java.util.List;

public record ClientGameState(
        PlayerID playerID,
        PlayerManager playerManager,
        EntityBoard entityBoard,
        PlayerFogOfWar fogOfWar,
        Terrain terrain,
        List<ActionRule> rules
) implements Serializable {
}
