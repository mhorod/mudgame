package core.server;


import core.EventOccurrenceSender;
import core.entities.EntityBoard;
import core.entities.model.Entity;
import core.events.EventObserver;
import core.events.PlayerEventObserver;
import core.fogofwar.FogOfWar;
import core.model.EntityID;
import core.model.PlayerID;
import core.terrain.TerrainGenerator;
import core.terrain.generators.SimpleLandGenerator;
import core.turns.PlayerManager;

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

    protected void initState(ServerGameState state) {

        playerManager = state.playerManager();
        entityBoard = state.entityBoard();
        fogOfWar = state.fogOfWar();

        eventSender = new EventOccurrenceSender();
        core = new ServerCore(state, eventSender);

        List<PlayerID> players = playerManager.getPlayerIDs();
        playerEventObservers = players.stream()
                .map(player -> new PlayerEventObserver(player, mock(EventObserver.class)))
                .toList();
        playerEventObservers.forEach(o -> eventSender.addObserver(o));

        eventObserver = mock(EventObserver.class);
        eventSender.addObserver(eventObserver);

    }

    public static Entity mockEntity(long entityID, long playerID) {
        return new Entity(List.of(), new EntityID(entityID), new PlayerID(playerID));
    }

    public static ServerCore newGame(int playerCount, EventOccurrenceObserver observer) {
        return new ServerCore(playerCount, observer, defaultTerrainGenerator());
    }

    private static TerrainGenerator defaultTerrainGenerator() {
        return new SimpleLandGenerator(2, 3, 50);
    }
}