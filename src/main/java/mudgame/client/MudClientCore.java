package mudgame.client;

import core.claiming.ClaimedAreaView;
import core.entities.EntityBoardView;
import core.event.Event;
import core.fogofwar.PlayerFogOfWarView;
import core.model.PlayerID;
import core.pathfinder.Pathfinder;
import core.pathfinder.PlayerPathfinder;
import core.spawning.PlayerSpawnManager;
import core.terrain.TerrainView;
import core.turns.PlayerTurnView;
import lombok.extern.slf4j.Slf4j;
import mudgame.client.events.EventProcessor;

@Slf4j
public class MudClientCore implements MudClientCoreView {
    private final ClientGameState state;
    private final EventProcessor eventProcessor;
    private final Pathfinder pathfinder;
    private final PlayerSpawnManager spawnManager;

    public MudClientCore(ClientGameState state) {
        this.state = state;
        eventProcessor = new EventProcessor(state);
        this.pathfinder = new PlayerPathfinder(
                state.playerID(),
                state.terrain(),
                state.entityBoard(),
                state.fogOfWar()
        );
        spawnManager = new PlayerSpawnManager(
                state.playerID(),
                state.entityBoard(),
                state.fogOfWar(),
                state.claimedArea(),
                state.terrain()
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

}
