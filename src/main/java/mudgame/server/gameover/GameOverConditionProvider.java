package mudgame.server.gameover;

import core.gameover.GameOverCondition;
import mudgame.server.state.ServerGameState;

@FunctionalInterface
public interface GameOverConditionProvider {
    GameOverCondition get(ServerGameState gameState);
}
