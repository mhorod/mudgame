package testutils.integration.utils;

import core.entities.EntityBoardView;
import core.gameover.GameOverCondition;
import core.model.PlayerID;
import mudgame.server.state.ServerState;

import java.util.List;
import java.util.Optional;

public class OwnThreeEntities implements GameOverCondition {
    private final List<PlayerID> players;
    private final EntityBoardView entityBoard;

    public OwnThreeEntities(ServerState state) {
        this.players = state.turnManager().players();
        this.entityBoard = state.entityBoard();
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
