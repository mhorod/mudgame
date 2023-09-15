package mudgame.server;

import core.claiming.ClaimedArea;
import core.claiming.ClaimedAreaView;
import core.entities.EntityBoard;
import core.entities.EntityBoardView;
import core.entities.model.Entity;
import core.event.Action;
import core.fogofwar.FogOfWar;
import core.fogofwar.FogOfWarView;
import core.model.PlayerID;
import core.model.Position;
import core.pathfinder.EntityPathfinder;
import core.pathfinder.Pathfinder;
import core.resources.ResourceManager;
import core.resources.ResourcesView;
import core.spawning.SpawnManager;
import core.terrain.TerrainView;
import core.terrain.generators.RectangleLandGenerator;
import core.terrain.generators.StartingTerrainGenerator;
import core.terrain.generators.TerrainGenerator;
import core.terrain.model.StartingTerrain;
import core.terrain.model.Terrain;
import core.terrain.placers.PlayerPlacer;
import core.terrain.placers.RandomPlayerPlacer;
import core.turns.TurnManager;
import core.turns.TurnView;
import lombok.extern.slf4j.Slf4j;
import mudgame.client.ClientGameState;
import mudgame.controls.actions.AttackEntityAction;
import mudgame.controls.actions.MoveEntity;
import mudgame.events.EventOccurrenceObserver;
import mudgame.server.actions.ActionProcessor;
import mudgame.server.rules.ActionRule;
import mudgame.server.rules.PlayerCanCreateEntity;
import mudgame.server.rules.attack.AttackedEntityIsInAttackRange;
import mudgame.server.rules.attack.AttackerSeesAttackedEntity;
import mudgame.server.rules.attack.PlayerCannotAttackOwnEntities;
import mudgame.server.rules.attack.PlayerOwnsAttackerEntity;
import mudgame.server.rules.movement.MoveDestinationIsEmpty;
import mudgame.server.rules.movement.MoveDestinationIsLand;
import mudgame.server.rules.movement.MoveDestinationIsReachable;
import mudgame.server.rules.movement.PlayerOwnsMovedEntity;
import mudgame.server.rules.movement.PlayerSeesMoveDestination;
import mudgame.server.rules.turn.PlayerTakesActionDuringOwnTurn;

import java.util.List;

import static core.entities.model.EntityType.BASE;
import static core.resources.ResourceType.MUD;
import static mudgame.server.rules.RuleGroup.groupRules;

@Slf4j
public final class MudServerCore {

    private final ServerGameState state;

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

    public MudServerCore(ServerGameState state, EventOccurrenceObserver eventOccurrenceObserver) {
        this.state = state;
        this.actionProcessor = new ActionProcessor(state, eventOccurrenceObserver);
    }

    private static ServerGameState newRawState(int playerCount, Terrain terrain) {
        TurnManager turnManager = new TurnManager(playerCount);
        FogOfWar fow = new FogOfWar(turnManager.players());
        EntityBoard entityBoard = new EntityBoard();
        ClaimedArea claimedArea = new ClaimedArea();
        ResourceManager resourceManager = new ResourceManager(turnManager.players());
        turnManager.players()
                .forEach(p -> resourceManager.add(p, 10, MUD));

        return new ServerGameState(
                turnManager,
                entityBoard,
                fow,
                terrain,
                claimedArea,
                resourceManager,
                defaultRules(turnManager, entityBoard, fow, resourceManager, terrain, claimedArea)
        );
    }

    public static ServerGameState newState(
            int playerCount, StartingTerrainGenerator terrainGenerator
    ) {
        StartingTerrain generatedTerrain = terrainGenerator.generate(playerCount);
        ServerGameState state = newRawState(playerCount, generatedTerrain.terrain());
        placePlayerBases(state, generatedTerrain.startingPositions());
        return state;
    }

    public static ServerGameState newState(int playerCount) {
        return newState(playerCount, defaultTerrainGenerator());
    }

    public void process(Action action, PlayerID actor) {
        if (action != null)
            actionProcessor.process(action, actor);
    }


    public ServerGameState state() {
        return state;
    }

    public static List<ActionRule> defaultRules(
            TurnView turnView,
            EntityBoardView entityBoard,
            FogOfWarView fow,
            ResourcesView resources,
            TerrainView terrain,
            ClaimedAreaView claimedArea
    ) {
        Pathfinder pathfinder = new EntityPathfinder(terrain, entityBoard, fow);
        SpawnManager spawnManager = new SpawnManager(
                entityBoard,
                fow,
                claimedArea,
                resources,
                terrain
        );

        return List.of(
                // turn rules
                new PlayerTakesActionDuringOwnTurn(turnView),

                // entity creation rules
                new PlayerCanCreateEntity(spawnManager),

                // entity movement rules
                groupRules(
                        new PlayerOwnsMovedEntity(entityBoard),
                        new PlayerSeesMoveDestination(fow),
                        new MoveDestinationIsEmpty(entityBoard),
                        new MoveDestinationIsLand(terrain),
                        new MoveDestinationIsReachable(pathfinder)
                ).forActions(MoveEntity.class),

                // attack rules
                groupRules(
                        new AttackerSeesAttackedEntity(entityBoard, fow),
                        new PlayerOwnsAttackerEntity(entityBoard),
                        new PlayerCannotAttackOwnEntities(entityBoard),
                        new AttackedEntityIsInAttackRange(entityBoard)
                ).forActions(AttackEntityAction.class)
        );
    }

    private static StartingTerrainGenerator defaultTerrainGenerator() {
        PlayerPlacer playerPlacer = new RandomPlayerPlacer(2, 4);
        TerrainGenerator terrainGenerator = new RectangleLandGenerator(100);

        return StartingTerrainGenerator.of(terrainGenerator, playerPlacer);
    }


    private static void placePlayerBases(ServerGameState state, List<Position> startingLocations) {
        for (int i = 0; i < startingLocations.size(); i++)
            placeBase(state, i, startingLocations.get(i));
    }

    private static void placeBase(ServerGameState state, int i, Position position) {
        PlayerID owner = state.turnManager().players().get(i);
        Entity entity = state.entityBoard().createEntity(BASE, owner, position);
        state.fogOfWar().playerFogOfWar(owner).placeEntity(entity, position);
        state.claimedArea().placeEntity(entity, position);
    }

    public ClientGameState clientState(PlayerID playerID) {
        return state.toClientGameState(playerID);
    }
}
