package core.client;

import core.PlayerManager;
import core.entities.EntityBoard;
import core.fogofwar.PlayerFogOfWar;
import core.model.PlayerID;
import core.rules.ActionRule;
import core.terrain.Terrain;

import java.util.List;

public record ClientGameState(
        PlayerID playerID,
        PlayerManager playerManager,
        EntityBoard entityBoard,
        PlayerFogOfWar fogOfWar,
        Terrain terrain,
        List<ActionRule> rules
) {
}
