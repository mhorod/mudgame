package core.client;

import core.entities.EntityBoard;
import core.entities.EntityMovementManager;
import core.fogofwar.PlayerFogOfWar;
import core.model.PlayerID;
import core.server.rules.ActionRule;
import core.terrain.Terrain;
import core.turns.PlayerManager;

import java.io.Serializable;
import java.util.List;

public record ClientGameState(
        PlayerID playerID,
        PlayerManager playerManager,
        EntityBoard entityBoard,
        PlayerFogOfWar fogOfWar,
        Terrain terrain,
        EntityMovementManager entityMovementManager,
        List<ActionRule> rules
) implements Serializable {
}
