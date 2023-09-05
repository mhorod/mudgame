package core.pathfinder;

import core.entities.EntityBoardView;
import core.entities.components.ComponentVisitor;
import core.entities.components.Movement;
import core.model.EntityID;
import core.model.Position;
import core.terrain.TerrainView;
import core.terrain.model.TerrainType;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.StandardException;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.PriorityQueue;
import java.util.Set;

import static java.util.Comparator.comparing;

@RequiredArgsConstructor
public class Pathfinder implements Serializable {
    private final TerrainView terrain;
    private final EntityBoardView entityBoard;

    private record PositionOnPath(Position position, int movementLeft) { }

    private class MovementVisitor implements ComponentVisitor<Integer>, Serializable {
        @Override
        public Integer visit(Movement m) {
            return m.getCurrentMovement();
        }
    }

    private final MovementVisitor movementVisitor = new MovementVisitor();

    @RequiredArgsConstructor
    public static class ReachablePositions {
        @Getter
        private final Set<Position> positions;
        private final Map<Position, Position> parents;

        private ReachablePositions(Map<Position, Position> parents) {
            positions = parents.keySet();
            this.parents = parents;
        }

        private static ReachablePositions empty() {
            return new ReachablePositions(Map.of());
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
        Optional<Integer> optionalMovement = getTurnMovement(entityID);
        if (optionalMovement.isEmpty())
            return ReachablePositions.empty();
        int movement = optionalMovement.get();

        Map<Position, Position> parents = new HashMap<>();
        PriorityQueue<PositionOnPath> awaiting = new PriorityQueue<>(
                comparing(p -> -p.movementLeft)
        );

        Position position = entityBoard.entityPosition(entityID);

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

    Optional<Integer> getTurnMovement(EntityID entityID) {
        return entityBoard.findEntityByID(entityID)
                .components()
                .stream()
                .map(movementVisitor::visit)
                .filter(Objects::nonNull)
                .findFirst();
    }


    List<PositionOnPath> neighbors(EntityID entityID, PositionOnPath p) {
        List<PositionOnPath> result = new ArrayList<>();
        for (int dx = -1; dx <= 1; dx++)
            for (int dy = -1; dy <= 1; dy++) {
                if (dx * dx == dy * dy)
                    continue;
                Position pos = p.position().plus(dx, dy);
                int cost = terrain.terrainAt(pos).getMovementCost();
                if (canMoveToPosition(entityID, p, pos))
                    result.add(new PositionOnPath(pos, p.movementLeft() - cost));
            }
        return result;
    }

    boolean canMoveToPosition(EntityID entityID, PositionOnPath current, Position target) {
        TerrainType t = terrain.terrainAt(target);
        if (t.getMovementCost() == -1)
            return false;
        else if (t.getMovementCost() > current.movementLeft())
            return false;
        else if (!entityBoard.entitiesAt(target).isEmpty())
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
