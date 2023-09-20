package core.gameover;

import core.model.PlayerID;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;

public interface GameOverCondition extends Serializable {
    boolean isGameOver();
    Optional<List<PlayerID>> winners();
}
