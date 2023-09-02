package core.pathfinder;

import core.entities.EntityBoardView;
import core.entities.EntityMovementManager;
import core.fogofwar.PlayerFogOfWar;
import core.model.EntityID;
import core.model.Position;
import core.terrain.TerrainView;
import lombok.RequiredArgsConstructor;
import lombok.experimental.StandardException;

import java.util.List;

@RequiredArgsConstructor
public class Pathfinder {
    private final TerrainView terrain;
    private final PlayerFogOfWar fow;
    private final EntityBoardView entityBoard;
    private final EntityMovementManager entityMovementManager;


    public List<Position> reachablePositions(EntityID entityID) {
        return List.of();
    }

    public List<Position> findPath(EntityID entityID, Position destination) {
        if (!isReachable(entityID, destination))
            throw new UnreachablePosition();
        return List.of();
    }

    public boolean isReachable(EntityID entityID, Position destination) {
        return reachablePositions(entityID).contains(destination);
    }

    @StandardException
    public static class UnreachablePosition extends RuntimeException { }
}
