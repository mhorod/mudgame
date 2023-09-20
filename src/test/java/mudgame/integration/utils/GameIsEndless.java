package mudgame.integration.utils;

import core.gameover.GameOverCondition;
import core.model.PlayerID;

import java.util.List;
import java.util.Optional;

public class GameIsEndless implements GameOverCondition {
    @Override
    public boolean isGameOver() {
        return false;
    }

    @Override
    public Optional<List<PlayerID>> winners() {
        return Optional.empty();
    }
}
