package core.terrain.placers;

import core.model.Position;
import core.terrain.model.Terrain;
import core.terrain.model.TerrainSize;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

@RequiredArgsConstructor
public class RandomPlayerPlacer implements PlayerPlacer {
    private final int minimumDistanceFromEdge;
    private final int minimumStartingDistance;

    private final Random random = new Random();

    public List<Position> placePlayers(int playerCount, Terrain terrain) {
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
