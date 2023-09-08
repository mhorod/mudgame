package core.terrain.generators;

import core.terrain.model.StartingTerrain;
import core.terrain.model.Terrain;
import core.terrain.placers.PlayerPlacer;

public interface StartingTerrainGenerator {
    StartingTerrain generate(int playerCount);
    static StartingTerrainGenerator of(TerrainGenerator generator, PlayerPlacer placer) {
        return playerCount -> {
            Terrain terrain = generator.generate(playerCount);
            return new StartingTerrain(terrain, placer.placePlayers(playerCount, terrain));
        };
    }
}
