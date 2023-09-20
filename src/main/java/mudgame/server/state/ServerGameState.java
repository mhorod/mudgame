package mudgame.server.state;

import core.claiming.ClaimedArea;
import core.entities.EntityBoard;
import core.fogofwar.FogOfWar;
import core.pathfinder.EntityPathfinder;
import core.pathfinder.Pathfinder;
import core.resources.ResourceManager;
import core.spawning.SpawnManager;
import core.terrain.model.Terrain;
import core.turns.TurnManager;

import java.io.Serializable;

public record ServerGameState(
        TurnManager turnManager,
        EntityBoard entityBoard,
        FogOfWar fogOfWar,
        Terrain terrain,
        ClaimedArea claimedArea,
        ResourceManager resourceManager
) implements Serializable {

    public static ServerGameState of(int playerCount, Terrain terrain) {
        TurnManager turnManager = new TurnManager(playerCount);
        return new ServerGameState(
                turnManager,
                new EntityBoard(),
                new FogOfWar(turnManager.players()),
                terrain,
                new ClaimedArea(),
                new ResourceManager(turnManager.players())
        );
    }

    public SpawnManager spawnManager() {
        return new SpawnManager(
                entityBoard,
                fogOfWar,
                claimedArea,
                resourceManager,
                terrain
        );
    }

    public Pathfinder pathfinder() {
        return new EntityPathfinder(
                terrain,
                entityBoard,
                fogOfWar
        );
    }
}
