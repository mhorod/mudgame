package core.terrain.generators;

import core.model.Position;
import core.terrain.model.Terrain;
import core.terrain.model.TerrainSize;
import core.terrain.model.TerrainType;
import lombok.RequiredArgsConstructor;

import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Generates rectangular terrain map consisting only of plain land
 */
@RequiredArgsConstructor
public class RectangleLandGenerator implements TerrainGenerator {

    private final int minimumTilesPerPlayer;

    @Override
    public Terrain generate(int playerCount) {
        if (playerCount <= 0)
            throw new IllegalArgumentException();

        int minimumArea = playerCount * minimumTilesPerPlayer;
        int side = (int) Math.ceil(Math.sqrt(minimumArea));

        TerrainSize size = new TerrainSize(side, side);

        Map<Position, TerrainType> terrainMap = IntStream.range(0, size.width())
                .boxed()
                .flatMap(x -> IntStream.range(0, size.height()).mapToObj(y -> new Position(x, y)))
                .collect(Collectors.toMap(pos -> pos, pos -> TerrainType.LAND));

        return new Terrain(size, terrainMap);

    }

}
