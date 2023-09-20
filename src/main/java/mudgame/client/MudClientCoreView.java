package mudgame.client;

import core.claiming.ClaimedAreaView;
import core.entities.EntityBoardView;
import core.fogofwar.PlayerFogOfWarView;
import core.model.PlayerID;
import core.pathfinder.Pathfinder;
import core.spawning.PlayerSpawnManager;
import core.terrain.TerrainView;
import core.turns.PlayerTurnView;

public interface MudClientCoreView {
    PlayerID myPlayerID();
    TerrainView terrain();
    Pathfinder pathfinder();
    PlayerSpawnManager spawnManager();
    ClaimedAreaView claimedArea();
    EntityBoardView entityBoard();
    PlayerFogOfWarView fogOfWar();
    PlayerTurnView turnView();
    PlayerAttackManager playerAttackManager();
}
