package mudgame.integration.utils;

import core.claiming.ClaimedArea;
import core.entities.EntityBoard;
import core.entities.model.Entity;
import core.event.Action;
import core.fogofwar.FogOfWar;
import core.fogofwar.FogOfWarView;
import core.model.PlayerID;
import core.model.Position;
import core.terrain.model.Terrain;
import core.terrain.model.TerrainType;
import core.turns.TurnManager;
import mudgame.server.ServerGameState;

@SuppressWarnings("unchecked")
public class Scenario<T extends Scenario<T>> {

    private TurnManager turnManager;
    private EntityBoard entityBoard;
    private FogOfWar fow;
    private Terrain terrain;
    private ClaimedArea claimedArea;
    private RuleProvider ruleProvider;


    public ScenarioGame act(PlayerID playerID, Action... actions) {
        ServerGameState state = serverState();
        return new ScenarioGame(state).act(playerID, actions);
    }

    private ServerGameState serverState() {
        return new ServerGameState(
                turnManager,
                entityBoard,
                fow,
                terrain,
                claimedArea,
                ruleProvider.rules(turnManager, entityBoard, fow, terrain, claimedArea)
        );
    }

    public ScenarioResult finish() {
        return new ScenarioGame(serverState()).finish();
    }

    public Scenario(int playerCount) {
        turnManager = new TurnManager(playerCount);
        entityBoard = new EntityBoard();
        fow = new FogOfWar(turnManager.getPlayerIDs());
        claimedArea = new ClaimedArea();
        ruleProvider = new DefaultRules();
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

}
