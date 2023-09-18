package mudgame.client;

import core.gameover.GameOverCondition;
import core.model.PlayerID;

import java.util.List;
import java.util.Optional;

public class ClientGameOverCondition implements GameOverCondition {
    private boolean isGameOver;
    private List<PlayerID> winners;

    public ClientGameOverCondition(GameOverCondition gameOverCondition) {
        isGameOver = gameOverCondition.isGameOver();
        winners = gameOverCondition.winners().orElse(null);
    }

    @Override
    public boolean isGameOver() {
        return isGameOver;
    }

    @Override
    public Optional<List<PlayerID>> winners() {
        return Optional.ofNullable(winners);
    }

    public void gameOver(List<PlayerID> winners) {
        this.isGameOver = true;
        this.winners = winners;
    }
}
