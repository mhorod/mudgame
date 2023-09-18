package mudgame.server.rules.turn;

import core.gameover.GameOverCondition;
import core.model.PlayerID;
import lombok.RequiredArgsConstructor;
import mudgame.controls.actions.Action;
import mudgame.server.rules.ActionRule;

@RequiredArgsConstructor
public class GameIsNotOver implements ActionRule {
    private final GameOverCondition gameOverCondition;

    @Override
    public boolean isSatisfied(Action action, PlayerID actor) {
        return !gameOverCondition.isGameOver();
    }
}
