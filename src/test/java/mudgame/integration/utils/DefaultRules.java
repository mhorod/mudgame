package mudgame.integration.utils;

import core.gameover.GameOverCondition;
import mudgame.server.MudServerCore;
import mudgame.server.rules.ActionRule;
import mudgame.server.state.ServerGameState;

import java.util.List;

public class DefaultRules implements RuleProvider {
    @Override
    public List<ActionRule> rules(
            ServerGameState gameState,
            GameOverCondition gameOverCondition
    ) {
        return MudServerCore.defaultRules(gameState, gameOverCondition);
    }
}
