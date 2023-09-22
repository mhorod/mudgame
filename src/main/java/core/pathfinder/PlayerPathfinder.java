package core.pathfinder;

import core.entities.EntityBoardView;
import core.fogofwar.FogOfWar;
import core.fogofwar.PlayerFogOfWar;
import core.model.EntityID;
import core.model.PlayerID;
import core.model.Position;
import core.terrain.TerrainView;
import core.turns.TurnView;

import java.util.List;

public class PlayerPathfinder implements Pathfinder {
    private final PlayerID playerID;
    private final EntityBoardView entityBoard;
    private final Pathfinder pathfinder;
    private final TurnView turnView;

    public PlayerPathfinder(
            PlayerID playerID,
            TerrainView terrain,
            EntityBoardView entityBoard,
            PlayerFogOfWar fow,
            TurnView turnView
    ) {
        this.playerID = playerID;
        this.entityBoard = entityBoard;
        this.turnView = turnView;
        this.pathfinder = new EntityPathfinder(terrain, entityBoard, FogOfWar.from(fow));
    }

    @Override
    public ReachablePositions reachablePositions(EntityID entityID) {
        if (!turnView.currentPlayer().equals(playerID))
            return ReachablePositions.empty();
        else if (!entityBoard.entityOwner(entityID).equals(playerID))
            return ReachablePositions.empty();
        else
            return pathfinder.reachablePositions(entityID);
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
