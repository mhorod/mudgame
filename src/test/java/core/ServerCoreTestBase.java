package core;


import core.entities.EntityBoard;
import core.entities.model.Entity;
import core.entities.model.EntityData;
import core.events.model.PlayerEventObserver;
import core.events.observers.EventObserver;
import core.events.observers.EventOccurrenceObserver;
import core.events.senders.EventOccurrenceSender;
import core.fogofwar.FogOfWar;
import core.model.EntityID;
import core.model.PlayerID;
import core.terrain.TerrainGenerator;
import core.terrain.generators.SimpleLandGenerator;
import org.junit.jupiter.api.BeforeEach;

import java.util.List;

import static org.mockito.Mockito.mock;

abstract class ServerCoreTestBase {
    PlayerManager playerManager;
    EntityBoard entityBoard;
    FogOfWar fogOfWar;
    ServerCore core;

    EventOccurrenceSender eventSender;
    List<PlayerEventObserver> playerEventObservers;
    EventObserver eventObserver;

    @BeforeEach
    void init() {
        eventSender = new EventOccurrenceSender();
        core = ServerCoreTestBase.newGame(4, eventSender);
        playerManager = core.state().playerManager();
        entityBoard = core.state().entityBoard();
        fogOfWar = core.state().fogOfWar();


        List<PlayerID> players = playerManager.getPlayerIDs();
        playerEventObservers = players.stream()
                .map(player -> new PlayerEventObserver(player, mock(EventObserver.class)))
                .toList();
        playerEventObservers.forEach(o -> eventSender.addObserver(o));

        eventObserver = mock(EventObserver.class);
        eventSender.addObserver(eventObserver);
    }

    public static Entity mockEntity(long entityID, long playerID) {
        return new Entity(mock(EntityData.class), new EntityID(entityID), new PlayerID(playerID));
    }

    public static ServerCore newGame(int playerCount, EventOccurrenceObserver observer) {
        GameState state = ServerCore.newGameState(playerCount, defaultTerrainGenerator());
        return new ServerCore(state, observer);
    }

    private static TerrainGenerator defaultTerrainGenerator() {
        return new SimpleLandGenerator(2, 3, 50);
    }
}