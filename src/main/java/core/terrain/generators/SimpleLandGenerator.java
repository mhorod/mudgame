package core.terrain.generators;

import core.model.Position;
import core.terrain.Terrain;
import core.terrain.TerrainGenerator;
import core.terrain.model.TerrainSize;
import core.terrain.model.TerrainType;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Generates rectangular terrain map consisting only of plain land
 */
@RequiredArgsConstructor
public class SimpleLandGenerator implements TerrainGenerator {
    private final int minimumDistanceFromEdge;
    private final int minimumStartingDistance;
    private final int minimumTilesPerPlayer;

    private final Random random = new Random();

    @Override
    public GeneratedTerrain generateTerrain(int playerCount) {
        if (playerCount <= 0)
            throw new IllegalArgumentException();
        else if (playerCount > 5)
            throw new UnsupportedPlayerCount();

        int minimumArea = playerCount * minimumTilesPerPlayer;
        int side = (int) Math.ceil(Math.sqrt(minimumArea));

        TerrainSize size = new TerrainSize(side, side);

        Map<Position, TerrainType> terrainMap = IntStream.range(0, size.width())
                .boxed()
                .flatMap(x -> IntStream.range(0, size.height()).mapToObj(y -> new Position(x, y)))
                .collect(Collectors.toMap(pos -> pos, pos -> TerrainType.LAND));

        Terrain terrain = new Terrain(size, terrainMap);

        return new GeneratedTerrain(placePlayers(playerCount, terrain), terrain);
    }

    private List<Position> placePlayers(int playerCount, Terrain terrain) {
        List<Position> selectedPositions = new ArrayList<>();
        List<Position> candidatePositions = getCandidatePositions(terrain);
        for (int i = 0; i < playerCount; i++)
            selectedPositions.add(placePlayer(candidatePositions));
        return selectedPositions;
    }

    private List<Position> getCandidatePositions(Terrain terrain) {
        List<Position> positions = new ArrayList<>();
        TerrainSize size = terrain.size();
        for (int x = minimumDistanceFromEdge; x < size.width() - minimumDistanceFromEdge; x++)
            for (int y = minimumDistanceFromEdge; y < size.height() - minimumDistanceFromEdge; y++)
                positions.add(new Position(x, y));
        return positions;
    }

    private Position placePlayer(List<Position> positions) {
        int index = random.nextInt(positions.size());
        Position position = positions.get(index);
        Set<Position> toRemove = getRemovedPositions(position);
        positions.removeAll(toRemove);
        return position;
    }

    private Set<Position> getRemovedPositions(Position position) {
        Set<Position> removedPositions = new HashSet<>();
        for (int dx = -minimumStartingDistance; dx <= minimumStartingDistance; dx++)
            for (int dy = -minimumStartingDistance; dy <= minimumStartingDistance; dy++)
                removedPositions.add(new Position(position.x() + dx, position.y() + dy));
        return removedPositions;
    }
}
