package core.terrain.generators;

import core.model.Position;
import core.terrain.model.Terrain;
import core.terrain.model.TerrainSize;
import core.terrain.model.TerrainType;
import lombok.RequiredArgsConstructor;

import java.util.Map;
import java.util.stream.IntStream;

import static java.util.stream.Collectors.toMap;

@RequiredArgsConstructor
public class FixedRectangleGenerator implements TerrainGenerator {
    private final TerrainSize size;
    private final TerrainType fill;

    @Override
    public Terrain generate(int playerCount) {
        Map<Position, TerrainType> terrainMap = IntStream.range(0, size.width())
                .boxed()
                .flatMap(x -> IntStream.range(0, size.height()).mapToObj(y -> new Position(x, y)))
                .collect(toMap(p -> p, p -> fill));
        return new Terrain(size, terrainMap);
    }
}
