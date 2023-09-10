package core.spawning;

import core.claiming.ClaimedAreaView;
import core.entities.EntityBoardView;
import core.entities.model.EntityType;
import core.fogofwar.FogOfWarView;
import core.model.PlayerID;
import core.model.Position;
import core.terrain.TerrainView;

import java.io.Serializable;
import java.util.Map;

import static java.util.stream.Collectors.toMap;

public class SpawnManager implements Serializable {
    private final Map<PlayerID, PlayerSpawnManager> playerSpawnManagers;


    public SpawnManager(
            EntityBoardView entityBoard,
            FogOfWarView fow,
            ClaimedAreaView claimedArea,
            TerrainView terrain
    ) {
        playerSpawnManagers = fow.players().stream()
                .collect(
                        toMap(
                                p -> p,
                                p -> new PlayerSpawnManager(
                                        p,
                                        entityBoard,
                                        fow.playerFogOfWarView(p),
                                        claimedArea,
                                        terrain
                                ))
                );
    }

    public boolean canSpawn(PlayerID player, EntityType type, Position position) {
        return playerSpawnManagers.get(player).allowedSpawnPositions(type).contains(position);
    }
}
