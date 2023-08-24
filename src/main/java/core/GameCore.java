package core;

import core.entities.EntityBoard;
import core.entities.EntityBoardView;
import core.entities.SimpleEntityBoard;
import core.events.Event;
import core.events.Event.Action;
import core.events.EventObserver;
import core.events.EventSender;
import core.fogofwar.FogOfWar;
import core.fogofwar.FogOfWarView;
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
import core.turns.TurnView;

import java.util.List;

public class GameCore implements ActionProcessor, EventObserver {
    private final GameState state;

    // event processing
    private final EventPlayerManager eventPlayerManager;
    private final EventEntityBoard eventEntityBoard;

    // rule processing
    private final RuleBasedActionProcessor actionProcessor;

    public GameCore(GameState state, EventSender eventSender) {
        this.state = state;
        eventPlayerManager = new EventPlayerManager(state.playerManager(), eventSender);
        eventEntityBoard = new EventEntityBoard(
                state.entityBoard(),
                state.fogOfWar(),
                eventSender
        );

        actionProcessor = new RuleBasedActionProcessor(state.rules());
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

    public static List<ActionRule> defaultRules(
            TurnView turnView,
            EntityBoardView entityBoard,
            FogOfWarView fow
    ) {
        return List.of(
                new PlayerTakesActionDuringOwnTurn(turnView),

                // entity rules
                new PlayerOwnsMovedEntity(entityBoard),
                new PlayerSeesMoveDestination(fow),
                new PlayerSeesCreationPosition(fow),
                new CreationPositionIsEmpty(entityBoard),
                new PlayerOwnsCreatedEntity(),
                new MoveDestinationIsEmpty(entityBoard)
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
                generatedTerrain.terrain(),
                defaultRules(playerManager, entityBoard, fow)
        );
    }
}
