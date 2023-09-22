package mudgame.client;

import core.claiming.ClaimedAreaView;
import core.entities.EntityBoardView;
import core.fogofwar.PlayerFogOfWarView;
import core.gameover.GameOverCondition;
import core.model.PlayerID;
import core.pathfinder.Pathfinder;
import core.pathfinder.PlayerPathfinder;
import core.resources.PlayerResourcesView;
import core.spawning.PlayerSpawnManager;
import core.terrain.TerrainView;
import core.turns.PlayerTurnView;
import lombok.extern.slf4j.Slf4j;
import mudgame.client.events.EventProcessor;
import mudgame.controls.events.Event;

@Slf4j
public class MudClientCore implements MudClientCoreView {
    private final ClientGameState state;
    private final EventProcessor eventProcessor;
    private final Pathfinder pathfinder;
    private final PlayerSpawnManager spawnManager;
    private final PlayerAttackManager playerAttackManager;

    public MudClientCore(ClientGameState state) {
        this.state = state;
        eventProcessor = new EventProcessor(state);
        this.pathfinder = new PlayerPathfinder(
                state.playerID(),
                state.terrain(),
                state.entityBoard(),
                state.fogOfWar(),
                state.turnManager()
        );
        spawnManager = new PlayerSpawnManager(
                state.playerID(),
                state.entityBoard(),
                state.fogOfWar(),
                state.claimedArea(),
                state.resourceManager(),
                state.terrain()
        );
        playerAttackManager = new PlayerAttackManager(
                state.playerID(),
                state.entityBoard(),
                state.turnManager()
        );
    }

    public void receive(Event event) {
        log.debug("Processing event: {}", event);
        eventProcessor.process(event);
    }

    public ClientGameState state() {
        return state;
    }

    @Override
    public PlayerID myPlayerID() {
        return state.playerID();
    }

    @Override
    public TerrainView terrain() {
        return state.terrain();
    }

    public Pathfinder pathfinder() {
        return pathfinder;
    }

    @Override
    public PlayerSpawnManager spawnManager() {
        return spawnManager;
    }

    @Override
    public EntityBoardView entityBoard() {
        return state.entityBoard();
    }

    @Override
    public ClaimedAreaView claimedArea() {
        return state.claimedArea();
    }

    @Override
    public PlayerFogOfWarView fogOfWar() {
        return state.fogOfWar();
    }

    @Override
    public PlayerTurnView turnView() {
        return state.turnManager();
    }

    @Override
    public PlayerAttackManager playerAttackManager() {
        return playerAttackManager;
    }

    @Override
    public PlayerResourcesView playerResources() {
        return state.resourceManager();
    }

    @Override
    public GameOverCondition gameOverCondition() {
        return state.gameOverCondition();
    }
}
