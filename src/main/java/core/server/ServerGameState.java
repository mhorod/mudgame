package core.server;

import core.client.ClientGameState;
import core.entities.EntityBoard;
import core.entities.model.Entity;
import core.fogofwar.FogOfWar;
import core.fogofwar.PlayerFogOfWar;
import core.model.PlayerID;
import core.model.Position;
import core.server.rules.ActionRule;
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

        EntityBoard newEntityBoard = new EntityBoard();
        for (Entity entity : entityBoard.allEntities()) {
            Position position = entityBoard.entityPosition(entity.id());
            if (newFogOfWar.isVisible(position))
                newEntityBoard.placeEntity(SerializationUtils.clone(entity), position);
        }

        Terrain newTerrain = terrain.applyFogOfWar(newFogOfWar);

        return new ClientGameState(
                playerID,
                newPlayerManager,
                newEntityBoard,
                newFogOfWar,
                newTerrain,
                rules
        );
    }
}
