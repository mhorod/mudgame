package mudgame.server;

import core.claiming.ClaimedArea;
import core.entities.EntityBoard;
import core.fogofwar.FogOfWar;
import core.fogofwar.PlayerFogOfWar;
import core.model.PlayerID;
import core.terrain.model.Terrain;
import core.turns.PlayerTurnManager;
import core.turns.TurnManager;
import mudgame.client.ClientGameState;
import mudgame.server.rules.ActionRule;
import org.apache.commons.lang3.SerializationUtils;

import java.io.Serializable;
import java.util.List;

public record ServerGameState(
        TurnManager turnManager,
        EntityBoard entityBoard,
        FogOfWar fogOfWar,
        Terrain terrain,
        ClaimedArea claimedArea,
        List<ActionRule> rules
) implements Serializable {
    public ClientGameState toClientGameState(PlayerID playerID) {
        PlayerTurnManager newTurnManager = new PlayerTurnManager(
                playerID,
                turnManager.getCurrentPlayer()
        );
        PlayerFogOfWar newFogOfWar = SerializationUtils.clone(fogOfWar.playerFogOfWar(playerID));


        return new ClientGameState(
                playerID,
                newTurnManager,
                entityBoard.applyFogOfWar(newFogOfWar),
                newFogOfWar,
                terrain.applyFogOfWar(newFogOfWar),
                claimedArea.mask(newFogOfWar, terrain),
                rules
        );
    }
}
