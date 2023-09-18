package mudgame.server.rules;

import core.gameover.GameOverCondition;
import mudgame.server.state.ServerGameState;

import java.util.List;

@FunctionalInterface
public interface RuleProvider {
    List<ActionRule> rules(
            ServerGameState gameState,
            GameOverCondition gameOverCondition
    );
}