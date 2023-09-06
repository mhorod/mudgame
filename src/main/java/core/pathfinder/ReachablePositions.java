package core.pathfinder;

import core.model.Position;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

@RequiredArgsConstructor
public class ReachablePositions {
    @Getter
    private final Set<Position> positions;
    private final Map<Position, Position> parents;

    ReachablePositions(Map<Position, Position> parents) {
        positions = parents.keySet();
        this.parents = parents;
    }

    static ReachablePositions empty() {
        return new ReachablePositions(Map.of());
    }

    public List<Position> getPath(Position destination) {
        if (!positions.contains(destination))
            throw new UnreachablePosition(destination);

        List<Position> path = new ArrayList<>();
        Position current = destination;
        while (current != null) {
            path.add(current);
            current = parents.get(current);
        }
        Collections.reverse(path);
        return path;
    }

    public boolean contains(Position position) { return positions.contains(position); }
}
