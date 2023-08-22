package core.terrain;

import core.model.Position;
import lombok.experimental.StandardException;

import java.util.List;

public interface TerrainGenerator
{
    @StandardException
    class UnsupportedPlayerCount extends RuntimeException { }

    record GeneratedTerrain(List<Position> startingLocations, Terrain terrain) { }

    GeneratedTerrain generateTerrain(int playerCount);
}
