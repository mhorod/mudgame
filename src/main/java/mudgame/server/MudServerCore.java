package mudgame.server;

import core.entities.EntityBoard;
import core.entities.EntityBoardView;
import core.entities.model.Entity;
import core.event.Action;
import core.fogofwar.FogOfWar;
import core.model.PlayerID;
import core.model.Position;
import core.pathfinder.EntityPathfinder;
import core.pathfinder.Pathfinder;
import core.terrain.TerrainView;
import core.terrain.generators.RectangleLandGenerator;
import core.terrain.generators.StartingTerrainGenerator;
import core.terrain.generators.TerrainGenerator;
import core.terrain.model.StartingTerrain;
import core.terrain.model.Terrain;
import core.terrain.placers.PlayerPlacer;
import core.terrain.placers.RandomPlayerPlacer;
import core.turns.PlayerManager;
import core.turns.TurnView;
import lombok.extern.slf4j.Slf4j;
import mudgame.controls.actions.AttackEntityAction;
import mudgame.controls.actions.CreateEntity;
import mudgame.controls.actions.MoveEntity;
import mudgame.events.EventOccurrenceObserver;
import mudgame.server.actions.ActionProcessor;
import mudgame.server.rules.ActionRule;
import mudgame.server.rules.AttackedEntityIsInAttackRange;
import mudgame.server.rules.AttackerEntityHasAttackComponent;
import mudgame.server.rules.AttackerSeesAttackedEntity;
import mudgame.server.rules.CreationPositionIsEmpty;
import mudgame.server.rules.CreationPositionIsLand;
import mudgame.server.rules.MoveDestinationIsEmpty;
import mudgame.server.rules.MoveDestinationIsLand;
import mudgame.server.rules.MoveDestinationIsReachable;
import mudgame.server.rules.PlayerCannotAttackOwnEntities;
import mudgame.server.rules.PlayerOwnsAttackerEntity;
import mudgame.server.rules.PlayerOwnsCreatedEntity;
import mudgame.server.rules.PlayerOwnsMovedEntity;
import mudgame.server.rules.PlayerSeesCreationPosition;
import mudgame.server.rules.PlayerSeesMoveDestination;
import mudgame.server.rules.PlayerTakesActionDuringOwnTurn;

import java.util.List;

import static core.entities.model.EntityType.BASE;
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
        this.actionProcessor = new ActionProcessor(state.rules(), state, eventOccurrenceObserver);
    }

    public List<PlayerID> players() {
        return state.playerManager().getPlayerIDs();
    }

    public MudServerCore(ServerGameState state, EventOccurrenceObserver eventOccurrenceObserver) {
        this.state = state;
        this.actionProcessor = new ActionProcessor(state.rules(), state, eventOccurrenceObserver);
    }

    private static ServerGameState newRawState(int playerCount, Terrain terrain) {
        PlayerManager playerManager = new PlayerManager(playerCount);
        FogOfWar fow = new FogOfWar(playerManager.getPlayerIDs());
        EntityBoard entityBoard = new EntityBoard();

        return new ServerGameState(
                playerManager,
                entityBoard,
                fow,
                terrain,
                defaultRules(playerManager, entityBoard, fow, terrain)
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
        actionProcessor.process(action, actor);
    }

    public ServerGameState state() {
        return state;
    }

    public static List<ActionRule> defaultRules(
            TurnView turnView,
            EntityBoardView entityBoard,
            FogOfWar fow,
            TerrainView terrain
    ) {
        Pathfinder pathfinder = new EntityPathfinder(terrain, entityBoard, fow);

        return List.of(
                // turn rules
                new PlayerTakesActionDuringOwnTurn(turnView),

                // entity creation rules
                groupRules(
                        new PlayerOwnsCreatedEntity(),
                        new CreationPositionIsEmpty(entityBoard),
                        new PlayerSeesCreationPosition(fow),
                        new CreationPositionIsLand(terrain)
                ).forActions(CreateEntity.class),

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
                        new AttackerEntityHasAttackComponent(entityBoard),
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
        PlayerID owner = state.playerManager().getPlayerIDs().get(i);
        Entity entity = state.entityBoard().createEntity(BASE, owner, position);
        state.fogOfWar().playerFogOfWar(owner).placeEntity(entity, position);
    }
}
