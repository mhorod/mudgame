package mudgame.server.state;

import core.claiming.ClaimedArea;
import core.entities.EntityBoard;
import core.fogofwar.FogOfWar;
import core.fogofwar.PlayerFogOfWar;
import core.gameover.GameOverCondition;
import core.model.PlayerID;
import core.resources.PlayerResourceManager;
import core.resources.ResourceManager;
import core.terrain.model.Terrain;
import core.turns.PlayerTurnManager;
import core.turns.TurnManager;
import mudgame.client.ClientGameOverCondition;
import mudgame.client.ClientGameState;
import mudgame.server.gameover.GameOverConditionProvider;
import mudgame.server.rules.ActionRule;
import mudgame.server.rules.RuleProvider;
import org.apache.commons.lang3.SerializationUtils;

import java.io.Serializable;
import java.util.List;

public record ServerState(
        ServerGameState gameState,
        GameOverCondition gameOverCondition,
        List<ActionRule> rules
) implements Serializable {

    public static ServerState of(
            int playerCount,
            Terrain terrain,
            GameOverConditionProvider gameOverConditionProvider,
            RuleProvider ruleProvider
    ) {
        ServerGameState gameState = ServerGameState.of(playerCount, terrain);
        GameOverCondition gameOverCondition = gameOverConditionProvider.get(gameState);
        return new ServerState(
                gameState,
                gameOverCondition,
                ruleProvider.rules(gameState, gameOverCondition)
        );
    }


    public ClientGameState toClientGameState(PlayerID playerID) {
        PlayerTurnManager newTurnManager = new PlayerTurnManager(
                playerID,
                turnManager().currentPlayer(),
                turnManager().currentTurn(),
                turnManager().playerCount()
        );
        PlayerFogOfWar newFogOfWar = SerializationUtils.clone(fogOfWar().playerFogOfWar(playerID));
        PlayerResourceManager newResources = SerializationUtils.clone(
                resourceManager().playerResources(playerID));


        return new ClientGameState(
                playerID,
                newTurnManager,
                entityBoard().applyFogOfWar(newFogOfWar),
                newFogOfWar,
                terrain().applyFogOfWar(newFogOfWar),
                claimedArea().mask(newFogOfWar, terrain()),
                newResources,
                new ClientGameOverCondition(gameOverCondition),
                rules
        );
    }

    public TurnManager turnManager() { return gameState.turnManager(); }

    public EntityBoard entityBoard() { return gameState.entityBoard(); }

    public FogOfWar fogOfWar() { return gameState.fogOfWar(); }

    public Terrain terrain() { return gameState.terrain(); }

    public ClaimedArea claimedArea() { return gameState.claimedArea(); }

    public ResourceManager resourceManager() { return gameState.resourceManager(); }
}
