package testutils.integration.utils;

import core.gameover.GameOverCondition;
import mudgame.server.state.ServerGameState;

public interface GameOverConditionProvider {
    GameOverCondition gameOverCondition(ServerGameState state);
}
