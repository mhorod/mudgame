package mudgame.server.gameover;

import core.entities.EntityBoardView;
import core.gameover.GameOverCondition;
import core.model.PlayerID;
import lombok.RequiredArgsConstructor;
import mudgame.server.state.ServerGameState;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
public class OwnsThreeEntities implements GameOverCondition {
    private final List<PlayerID> players;
    private final EntityBoardView entityBoard;

    public OwnsThreeEntities(ServerGameState gameState) {
        players = gameState.turnManager().players();
        entityBoard = gameState.entityBoard();
    }

    @Override
    public boolean isGameOver() {
        return players
                .stream()
                .anyMatch(p -> entityBoard.playerEntities(p).size() >= 3);
    }

    @Override
    public Optional<List<PlayerID>> winners() {
        return Optional.empty();
    }

}
