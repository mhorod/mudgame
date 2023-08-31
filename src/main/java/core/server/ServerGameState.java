package core.server;

import core.client.ClientGameState;
import core.entities.EntityBoard;
import core.entities.model.Entity;
import core.fogofwar.FogOfWar;
import core.fogofwar.FogOfWarView;
import core.fogofwar.PlayerFogOfWar;
import core.model.PlayerID;
import core.model.Position;
import core.server.rules.ActionRule;
import core.terrain.Terrain;
import core.turns.PlayerManager;

import java.io.Serializable;
import java.util.List;
import java.util.stream.Collectors;

public record ServerGameState(
        PlayerManager playerManager,
        EntityBoard entityBoard,
        FogOfWar fogOfWar,
        Terrain terrain,
        List<ActionRule> rules
) implements Serializable {
    public ClientGameState toClientGameState(PlayerID playerID) {
        PlayerFogOfWar playerFogOfWar = fogOfWar.playerView(playerID);

        EntityBoard playerEntityBoard = new EntityBoard();
        for (Entity entity : entityBoard.allEntities()) {
            Position position = entityBoard.entityPosition(entity.id());
            if (playerFogOfWar.isVisible(position))
                playerEntityBoard.placeEntity(entity, position);
        }

        Terrain playerTerrain = new Terrain(
                terrain().size(),
                playerFogOfWar
                        .visiblePositions()
                        .stream()
                        .collect(Collectors.toMap(
                                position -> position,
                                terrain::terrainAt
                        ))
        );

        return new ClientGameState(
                playerID,
                playerManager,
                playerEntityBoard,
                playerFogOfWar,
                playerTerrain,
                rules
        );
    }
}
