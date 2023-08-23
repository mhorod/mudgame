package core;


import core.entities.EntityBoard;
import core.entities.model.Entity;
import core.entities.model.EntityData;
import core.events.EventObserver;
import core.events.EventSender;
import core.events.ObserverEventSender;
import core.events.PlayerEventObserver;
import core.fogofwar.FogOfWar;
import core.model.EntityID;
import core.model.PlayerID;
import core.terrain.TerrainGenerator;
import core.terrain.generators.SimpleLandGenerator;
import org.junit.jupiter.api.BeforeEach;

import java.util.List;

import static org.mockito.Mockito.mock;

abstract class GameCoreTestBase {
    PlayerManager playerManager;
    EntityBoard entityBoard;
    FogOfWar fogOfWar;
    GameCore core;

    ObserverEventSender eventSender;
    List<PlayerEventObserver> playerEventObservers;
    EventObserver eventObserver;

    @BeforeEach
    void init() {
        eventSender = new ObserverEventSender();
        core = GameCoreTestBase.newGame(4, eventSender);
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

    public static GameCore newGame(int playerCount, EventSender sender) {
        TerrainGenerator terrainGenerator = new SimpleLandGenerator(2, 3, 50);
        GameState state = GameCore.newGameState(playerCount, terrainGenerator);
        return new GameCore(state, sender, GameCore.defaultRules(state));
    }
}