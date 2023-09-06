package core.pathfinder;

import core.model.EntityID;
import core.model.Position;

import java.util.List;

public interface Pathfinder {
    ReachablePositions reachablePositions(EntityID entityID);
    List<Position> findPath(EntityID entityID, Position destination);
    boolean isReachable(EntityID entityID, Position destination);
}
