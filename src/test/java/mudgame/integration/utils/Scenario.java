package mudgame.integration.utils;

import core.claiming.ClaimedArea;
import core.entities.EntityBoard;
import core.entities.model.Entity;
import core.fogofwar.FogOfWar;
import core.fogofwar.FogOfWarView;
import core.gameover.GameOverCondition;
import core.model.PlayerID;
import core.model.Position;
import core.resources.ResourceManager;
import core.resources.ResourceType;
import core.terrain.model.Terrain;
import core.terrain.model.TerrainType;
import core.turns.TurnManager;
import mudgame.controls.actions.Action;
import mudgame.server.rules.DefaultRules;
import mudgame.server.rules.RuleProvider;
import mudgame.server.state.ServerGameState;
import mudgame.server.state.ServerState;

@SuppressWarnings("unchecked")
public class Scenario<T extends Scenario<T>> {

    private TurnManager turnManager;
    private EntityBoard entityBoard;
    private FogOfWar fow;
    private Terrain terrain;
    private ClaimedArea claimedArea;
    private ResourceManager resourceManager;
    private RuleProvider ruleProvider;
    private GameOverConditionProvider gameOverConditionProvider;

    public ScenarioGame act(PlayerID playerID, Action... actions) {
        ServerState state = serverState();
        return new ScenarioGame(state).act(playerID, actions);
    }

    private ServerState serverState() {
        ServerGameState gameState = new ServerGameState(
                turnManager,
                entityBoard,
                fow,
                terrain,
                claimedArea,
                resourceManager
        );
        GameOverCondition gameOverCondition = gameOverConditionProvider.gameOverCondition(
                gameState);
        return new ServerState(
                gameState,
                gameOverCondition,
                ruleProvider.rules(gameState, gameOverCondition)
        );
    }

    public Scenario(int playerCount) {
        turnManager = new TurnManager(playerCount);
        entityBoard = new EntityBoard();
        fow = new FogOfWar(turnManager.players());
        claimedArea = new ClaimedArea();
        ruleProvider = new DefaultRules();
        resourceManager = new ResourceManager(turnManager.players());
        gameOverConditionProvider = gameState -> new GameIsEndless();
    }


    public T with(Terrain terrain) {
        this.terrain = terrain;
        return (T) this;
    }

    public T with(ClaimedArea claimedArea) {
        this.claimedArea = claimedArea;
        return (T) this;
    }

    public T with(Entity entity, Position position) {
        entityBoard.placeEntity(entity, position);
        fow.placeEntity(entity, position);
        claimedArea.placeEntity(entity, position);
        return (T) this;
    }

    public T with(TerrainType terrainType, Position position) {
        terrain.setTerrainAt(position, terrainType);
        return (T) this;
    }

    public FogOfWarView fow() { return fow; }

    public T withResources(PlayerID player, ResourceType mud, int amount) {
        resourceManager.set(player, mud, amount);
        return (T) this;
    }

    public T with(GameOverConditionProvider gameOverConditionProvider) {
        this.gameOverConditionProvider = gameOverConditionProvider;
        return (T) this;
    }

}
