package mudgame.integration.utils;

import core.gameover.GameOverCondition;
import mudgame.server.rules.ActionRule;
import mudgame.server.state.ServerGameState;

import java.util.List;

public interface RuleProvider {
    List<ActionRule> rules(
            ServerGameState gameState,
            GameOverCondition gameOverCondition
    );
}