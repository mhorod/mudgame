package core.server;

import core.entities.EntityBoard;
import core.fogofwar.FogOfWar;
import core.server.rules.ActionRule;
import core.terrain.Terrain;
import core.turns.PlayerManager;

import java.io.Serializable;
import java.util.List;

public record ServerGameState(
        PlayerManager playerManager,
        EntityBoard entityBoard,
        FogOfWar fogOfWar,
        Terrain terrain,
        List<ActionRule> rules
) implements Serializable {
}
