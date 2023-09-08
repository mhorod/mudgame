package core.terrain.generators;

import core.terrain.model.Terrain;

public interface TerrainGenerator {
    Terrain generate(int playerCount);
}
