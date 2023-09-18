package mudgame.server;

import core.entities.model.Entity;
import core.gameover.GameOverCondition;
import core.model.PlayerID;
import core.model.Position;
import core.pathfinder.Pathfinder;
import core.spawning.SpawnManager;
import core.terrain.generators.RectangleLandGenerator;
import core.terrain.generators.StartingTerrainGenerator;
import core.terrain.generators.TerrainGenerator;
import core.terrain.model.StartingTerrain;
import core.terrain.model.Terrain;
import core.terrain.placers.PlayerPlacer;
import core.terrain.placers.RandomPlayerPlacer;
import lombok.extern.slf4j.Slf4j;
import mudgame.client.ClientGameState;
import mudgame.controls.actions.Action;
import mudgame.controls.actions.AttackEntityAction;
import mudgame.controls.actions.MoveEntity;
import mudgame.server.actions.ActionProcessor;
import mudgame.server.gameover.OwnsThreeEntities;
import mudgame.server.rules.ActionRule;
import mudgame.server.rules.attack.AttackedEntityIsInAttackRange;
import mudgame.server.rules.attack.AttackerSeesAttackedEntity;
import mudgame.server.rules.attack.PlayerCannotAttackOwnEntities;
import mudgame.server.rules.attack.PlayerOwnsAttackerEntity;
import mudgame.server.rules.creation.PlayerCanCreateEntity;
import mudgame.server.rules.movement.MoveDestinationIsEmpty;
import mudgame.server.rules.movement.MoveDestinationIsLand;
import mudgame.server.rules.movement.MoveDestinationIsReachable;
import mudgame.server.rules.movement.PlayerOwnsMovedEntity;
import mudgame.server.rules.movement.PlayerSeesMoveDestination;
import mudgame.server.rules.turn.GameIsNotOver;
import mudgame.server.rules.turn.PlayerTakesActionDuringOwnTurn;
import mudgame.server.state.ServerGameState;
import mudgame.server.state.ServerState;

import java.util.List;

import static core.entities.model.EntityType.BASE;
import static core.resources.ResourceType.MUD;
import static mudgame.server.rules.RuleGroup.groupRules;

@Slf4j
public final class MudServerCore {

    private final ServerState state;

    // action processing
    private final ActionProcessor actionProcessor;

    public MudServerCore(int playerCount) {
        this(playerCount, e -> { });
    }

    public MudServerCore(int playerCount, EventOccurrenceObserver eventOccurrenceObserver) {
        this(playerCount, eventOccurrenceObserver, defaultTerrainGenerator());
    }

    public MudServerCore(
            int playerCount,
            EventOccurrenceObserver eventOccurrenceObserver,
            StartingTerrainGenerator terrainGenerator
    ) {
        this.state = newState(playerCount, terrainGenerator);
        this.actionProcessor = new ActionProcessor(state, eventOccurrenceObserver);
    }

    public List<PlayerID> players() {
        return state.turnManager().players();
    }

    public MudServerCore(ServerState state, EventOccurrenceObserver eventOccurrenceObserver) {
        this.state = state;
        this.actionProcessor = new ActionProcessor(state, eventOccurrenceObserver);
    }

    private static ServerState newRawState(int playerCount, Terrain terrain) {
        ServerGameState gameState = ServerGameState.of(playerCount, terrain);

        GameOverCondition gameOverCondition = new OwnsThreeEntities(
                gameState.turnManager().players(),
                gameState.entityBoard()
        );

        return new ServerState(
                gameState,
                gameOverCondition,
                defaultRules(gameState, gameOverCondition)
        );
    }

    public static ServerState newState(
            int playerCount, StartingTerrainGenerator terrainGenerator
    ) {
        StartingTerrain generatedTerrain = terrainGenerator.generate(playerCount);
        ServerState state = newRawState(playerCount, generatedTerrain.terrain());
        placePlayerBases(state, generatedTerrain.startingPositions());
        initializeResources(state);
        return state;
    }

    private static void initializeResources(ServerState state) {
        state.turnManager().players()
                .forEach(p -> state.resourceManager().add(p, 10, MUD));
    }

    public static ServerState newState(int playerCount) {
        return newState(playerCount, defaultTerrainGenerator());
    }

    public void process(Action action, PlayerID actor) {
        if (action != null)
            actionProcessor.process(action, actor);
    }


    public ServerState state() {
        return state;
    }

    public static List<ActionRule> defaultRules(
            ServerGameState gameState,
            GameOverCondition gameOverCondition
    ) {
        Pathfinder pathfinder = gameState.pathfinder();
        SpawnManager spawnManager = gameState.spawnManager();

        return List.of(
                // turn rules
                new PlayerTakesActionDuringOwnTurn(gameState.turnManager()),
                new GameIsNotOver(gameOverCondition),

                // entity creation rules
                new PlayerCanCreateEntity(spawnManager),

                // entity movement rules
                groupRules(
                        new PlayerOwnsMovedEntity(gameState.entityBoard()),
                        new PlayerSeesMoveDestination(gameState.fogOfWar()),
                        new MoveDestinationIsEmpty(gameState.entityBoard()),
                        new MoveDestinationIsLand(gameState.terrain()),
                        new MoveDestinationIsReachable(pathfinder)
                ).forActions(MoveEntity.class),

                // attack rules
                groupRules(
                        new AttackerSeesAttackedEntity(gameState.entityBoard(),
                                                       gameState.fogOfWar()),
                        new PlayerOwnsAttackerEntity(gameState.entityBoard()),
                        new PlayerCannotAttackOwnEntities(gameState.entityBoard()),
                        new AttackedEntityIsInAttackRange(gameState.entityBoard())
                ).forActions(AttackEntityAction.class)
        );
    }

    private static StartingTerrainGenerator defaultTerrainGenerator() {
        PlayerPlacer playerPlacer = new RandomPlayerPlacer(2, 4);
        TerrainGenerator terrainGenerator = new RectangleLandGenerator(100);

        return StartingTerrainGenerator.of(terrainGenerator, playerPlacer);
    }


    private static void placePlayerBases(ServerState state, List<Position> startingLocations) {
        for (int i = 0; i < startingLocations.size(); i++)
            placeBase(state, i, startingLocations.get(i));
    }

    private static void placeBase(ServerState state, int i, Position position) {
        PlayerID owner = state.turnManager().players().get(i);
        Entity entity = state.entityBoard().createEntity(BASE, owner, position);
        state.fogOfWar().playerFogOfWar(owner).placeEntity(entity, position);
        state.claimedArea().placeEntity(entity, position);
    }

    public ClientGameState clientState(PlayerID playerID) {
        return state.toClientGameState(playerID);
    }
}
