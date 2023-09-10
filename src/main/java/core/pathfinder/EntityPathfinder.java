package core.pathfinder;

import core.entities.EntityBoardView;
import core.entities.components.ComponentVisitor;
import core.entities.components.Movement;
import core.entities.model.Entity;
import core.fogofwar.FogOfWarView;
import core.model.EntityID;
import core.model.Position;
import core.terrain.TerrainView;
import core.terrain.model.TerrainType;
import lombok.RequiredArgsConstructor;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.PriorityQueue;

import static java.util.Comparator.comparing;

@RequiredArgsConstructor
public class EntityPathfinder implements Pathfinder, Serializable {
    private final TerrainView terrain;
    private final EntityBoardView entityBoard;
    private final FogOfWarView fow;

    private record PositionOnPath(Position position, int movementLeft) { }

    private static class MovementVisitor implements ComponentVisitor<Integer>, Serializable {
        @Override
        public Integer visit(Movement m) {
            return m.getCurrentMovement();
        }
    }

    private final MovementVisitor movementVisitor = new MovementVisitor();

    public List<Position> findPath(EntityID entityID, Position destination) {
        return reachablePositions(entityID).getPath(destination);
    }


    public ReachablePositions reachablePositions(EntityID entityID) {
        Entity entity = entityBoard.findEntityByID(entityID);
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
            for (PositionOnPath p : neighbors(entity, current)) {
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


    List<PositionOnPath> neighbors(Entity entity, PositionOnPath p) {
        List<PositionOnPath> result = new ArrayList<>();
        for (int dx = -1; dx <= 1; dx++)
            for (int dy = -1; dy <= 1; dy++) {
                if (dx * dx == dy * dy)
                    continue;
                Position pos = p.position().plus(dx, dy);
                int cost = terrain.terrainAt(pos).getMovementCost();
                if (canMoveToPosition(entity, p, pos))
                    result.add(new PositionOnPath(pos, p.movementLeft() - cost));
            }
        return result;
    }

    boolean canMoveToPosition(Entity entity, PositionOnPath current, Position destination) {
        TerrainType t = terrain.terrainAt(destination);
        if (t.getMovementCost() == -1)
            return false;
        else if (t.getMovementCost() > current.movementLeft())
            return false;
        else if (!entityBoard.entitiesAt(destination).isEmpty())
            return false;
        else if (!fow.playerSees(destination, entity.owner()))
            return false;
        else
            return true;
    }

    public boolean isReachable(EntityID entityID, Position destination) {
        return reachablePositions(entityID).contains(destination);
    }

}
