package mudgame.client;

import core.claiming.PlayerClaimedArea;
import core.entities.EntityBoard;
import core.fogofwar.PlayerFogOfWar;
import core.model.PlayerID;
import core.terrain.model.Terrain;
import core.turns.PlayerTurnManager;
import mudgame.server.rules.ActionRule;

import java.io.Serializable;
import java.util.List;

public record ClientGameState(PlayerID playerID,
                              PlayerTurnManager turnManager,
                              EntityBoard entityBoard,
                              PlayerFogOfWar fogOfWar,
                              Terrain terrain,
                              PlayerClaimedArea claimedArea,
                              List<ActionRule> rules)
        implements Serializable {
}
