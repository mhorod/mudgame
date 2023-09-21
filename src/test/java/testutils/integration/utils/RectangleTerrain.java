package testutils.integration.utils;

import core.model.Position;
import core.terrain.model.Terrain;
import core.terrain.model.TerrainSize;
import core.terrain.model.TerrainType;
import lombok.experimental.UtilityClass;

import java.util.Map;
import java.util.stream.IntStream;

import static core.terrain.model.TerrainType.LAND;
import static java.util.stream.Collectors.toMap;

@UtilityClass
public final class RectangleTerrain {
    public static Terrain land(int width, int height) {
        return RectangleTerrain.of(new TerrainSize(width, height), LAND);
    }

    public static Terrain of(TerrainSize size, TerrainType fill) {
        Map<Position, TerrainType> terrainMap = IntStream.range(0, size.width())
                .boxed()
                .flatMap(x -> IntStream.range(0, size.height()).mapToObj(y -> new Position(x, y)))
                .collect(toMap(p -> p, p -> fill));
        return new Terrain(size, terrainMap);
    }
}
