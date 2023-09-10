package mudgame.client;

import core.event.Event;
import core.pathfinder.Pathfinder;
import core.pathfinder.PlayerPathfinder;
import core.spawning.PlayerSpawnManager;
import lombok.extern.slf4j.Slf4j;
import mudgame.client.events.EventProcessor;

@Slf4j
public class MudClientCore {
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

    public Pathfinder pathfinder() {
        return pathfinder;
    }

    public PlayerSpawnManager spawnManager() {
        return spawnManager;
    }
}
