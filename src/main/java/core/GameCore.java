package core;

import core.entities.EntityBoard;
import core.entities.SimpleEntityBoard;
import core.events.Event;
import core.events.Event.Action;
import core.events.EventObserver;
import core.events.EventSender;
import core.fogofwar.FogOfWar;
import core.model.PlayerID;
import core.rules.ActionRule;
import core.rules.CreationPositionIsEmpty;
import core.rules.MoveDestinationIsEmpty;
import core.rules.PlayerOwnsCreatedEntity;
import core.rules.PlayerOwnsMovedEntity;
import core.rules.PlayerSeesCreationPosition;
import core.rules.PlayerSeesMoveDestination;
import core.rules.PlayerTakesActionDuringOwnTurn;
import core.terrain.TerrainGenerator;
import core.terrain.TerrainGenerator.GeneratedTerrain;
import core.terrain.generators.SimpleLandGenerator;

import java.util.List;

public class GameCore implements ActionProcessor, EventObserver {
    private final GameState state;

    // event processing
    private final EventPlayerManager eventPlayerManager;
    private final EventEntityBoard eventEntityBoard;

    // rule processing
    public final List<ActionRule> rules;
    private final RuleBasedActionProcessor actionProcessor;

    public GameCore(GameState state, EventSender eventSender, List<ActionRule> rules) {
        this.state = state;
        eventPlayerManager = new EventPlayerManager(state.playerManager(), eventSender);
        eventEntityBoard = new EventEntityBoard(
                state.entityBoard(),
                state.fogOfWar(),
                eventSender
        );

        this.rules = rules;
        actionProcessor = new RuleBasedActionProcessor(rules);
        actionProcessor.addObservers(eventPlayerManager, eventEntityBoard);
    }


    @Override
    public void process(Action action, PlayerID actor) {
        actionProcessor.process(action, actor);
    }

    public GameState state() {
        return state;
    }

    @Override
    public void receive(Event event) {
        eventPlayerManager.receive(event);
        eventEntityBoard.receive(event);
    }

    public static List<ActionRule> defaultRules(GameState state) {
        return List.of(
                new PlayerTakesActionDuringOwnTurn(state.playerManager()),

                // entity rules
                new PlayerOwnsMovedEntity(state.entityBoard()),
                new PlayerSeesMoveDestination(state.fogOfWar()),
                new PlayerSeesCreationPosition(state.fogOfWar()),
                new CreationPositionIsEmpty(state.entityBoard()),
                new PlayerOwnsCreatedEntity(),
                new MoveDestinationIsEmpty(state.entityBoard())
        );
    }

    public static GameState newGameState(int playerCount) {
        return newGameState(playerCount, defaultTerrainGenerator());
    }

    private static TerrainGenerator defaultTerrainGenerator() {
        return new SimpleLandGenerator(2, 3, 50);
    }

    public static GameState newGameState(int playerCount, TerrainGenerator terrainGenerator) {
        PlayerManager playerManager = new PlayerManager(playerCount);
        FogOfWar fow = new FogOfWar(playerManager.getPlayerIDs());
        EntityBoard entityBoard = new SimpleEntityBoard();
        GeneratedTerrain generatedTerrain = terrainGenerator.generateTerrain(playerCount);
        return new GameState(
                playerManager,
                entityBoard,
                fow,
                generatedTerrain.terrain()
        );
    }
}
