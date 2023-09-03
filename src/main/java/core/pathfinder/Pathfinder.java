package core.pathfinder;

import core.entities.EntityBoardView;
import core.entities.EntityMovementManager;
import core.model.EntityID;
import core.model.Position;
import core.terrain.TerrainView;
import core.terrain.model.TerrainType;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.StandardException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;

import static java.util.Comparator.comparing;

@RequiredArgsConstructor
public class Pathfinder {
    private final TerrainView terrain;
    private final EntityBoardView entityBoard;
    private final EntityMovementManager entityMovementManager;

    private record PositionOnPath(Position position, int movementLeft) { }

    @RequiredArgsConstructor
    public static class ReachablePositions {
        @Getter
        private final Set<Position> positions;
        private final Map<Position, Position> parents;

        private ReachablePositions(Map<Position, Position> parents) {
            positions = parents.keySet();
            this.parents = parents;
        }

        public List<Position> getPath(Position destination) {
            if (!positions.contains(destination))
                throw new UnreachablePosition();

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

    public List<Position> findPath(EntityID entityID, Position destination) {
        return reachablePositions(entityID).getPath(destination);
    }


    public ReachablePositions reachablePositions(EntityID entityID) {
        Map<Position, Position> parents = new HashMap<>();
        PriorityQueue<PositionOnPath> awaiting = new PriorityQueue<>(
                comparing(p -> -p.movementLeft)
        );

        Position position = entityBoard.entityPosition(entityID);
        int movement = entityMovementManager.getTurnMovement(entityID);

        parents.put(position, null);
        awaiting.add(new PositionOnPath(position, movement));

        while (!awaiting.isEmpty()) {
            PositionOnPath current = awaiting.remove();
            for (PositionOnPath p : neighbors(entityID, current)) {
                if (!parents.containsKey(p.position)) {
                    parents.put(p.position, current.position);
                    awaiting.add(p);
                }
            }
        }
        return new ReachablePositions(parents);
    }

    List<PositionOnPath> neighbors(EntityID entityID, PositionOnPath p) {
        List<PositionOnPath> result = new ArrayList<>();
        for (int dx = -1; dx <= 1; dx++)
            for (int dy = -1; dy <= 1; dy++) {
                Position pos = p.position().plus(dx, dy);
                int cost = terrain.terrainAt(pos).getMovementCost();
                if (canMoveToPosition(entityID, p))
                    result.add(new PositionOnPath(pos, p.movementLeft() - cost));
            }
        return result;
    }

    boolean canMoveToPosition(EntityID entityID, PositionOnPath p) {
        TerrainType t = terrain.terrainAt(p.position());
        if (t.getMovementCost() == -1)
            return false;
        else if (t.getMovementCost() > p.movementLeft())
            return false;
        else if (!entityBoard.entitiesAt(p.position()).isEmpty())
            return false;
        else
            return true;
    }

    public boolean isReachable(EntityID entityID, Position destination) {
        return reachablePositions(entityID).contains(destination);
    }

    @StandardException
    public static class UnreachablePosition extends RuntimeException { }
}
