package mudgame.server;

import mudgame.client.ClientGameState;
import core.entities.EntityBoard;
import core.fogofwar.FogOfWar;
import core.fogofwar.PlayerFogOfWar;
import core.model.PlayerID;
import mudgame.server.rules.ActionRule;
import core.terrain.Terrain;
import core.turns.PlayerManager;
import org.apache.commons.lang3.SerializationUtils;

import java.io.Serializable;
import java.util.List;

public record ServerGameState(
        PlayerManager playerManager,
        EntityBoard entityBoard,
        FogOfWar fogOfWar,
        Terrain terrain,
        List<ActionRule> rules
) implements Serializable {
    public ClientGameState toClientGameState(PlayerID playerID) {
        PlayerManager newPlayerManager = SerializationUtils.clone(playerManager);
        PlayerFogOfWar newFogOfWar = SerializationUtils.clone(fogOfWar.playerFogOfWar(playerID));


        return new ClientGameState(
                playerID,
                newPlayerManager,
                entityBoard.applyFogOfWar(newFogOfWar),
                newFogOfWar,
                terrain.applyFogOfWar(newFogOfWar),
                rules
        );
    }
}
