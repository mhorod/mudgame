package core.pathfinder;

import core.entities.EntityBoardView;
import core.fogofwar.FogOfWar;
import core.fogofwar.PlayerFogOfWar;
import core.model.EntityID;
import core.model.PlayerID;
import core.model.Position;
import core.terrain.TerrainView;

import java.util.List;

public class PlayerPathfinder implements Pathfinder {
    private final PlayerID playerID;
    private final EntityBoardView entityBoard;
    private final Pathfinder pathfinder;

    public PlayerPathfinder(
            PlayerID playerID,
            TerrainView terrain,
            EntityBoardView entityBoard,
            PlayerFogOfWar fow
    ) {
        this.playerID = playerID;
        this.entityBoard = entityBoard;
        this.pathfinder = new EntityPathfinder(terrain, entityBoard, FogOfWar.from(fow));
    }

    @Override
    public ReachablePositions reachablePositions(EntityID entityID) {
        if (entityBoard.entityOwner(entityID).equals(playerID))
            return pathfinder.reachablePositions(entityID);
        else
            return ReachablePositions.empty();
    }

    @Override
    public List<Position> findPath(
            EntityID entityID, Position destination
    ) {
        if (entityBoard.entityOwner(entityID).equals(playerID))
            return pathfinder.findPath(entityID, destination);
        else
            throw new UnreachablePosition(entityID, destination);
    }

    @Override
    public boolean isReachable(EntityID entityID, Position destination) {
        if (entityBoard.entityOwner(entityID).equals(playerID))
            return pathfinder.isReachable(entityID, destination);
        else
            return false;
    }
}
