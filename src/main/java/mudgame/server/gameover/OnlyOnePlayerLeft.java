package mudgame.server.gameover;

import core.entities.EntityBoardView;
import core.entities.model.Entity;
import core.gameover.GameOverCondition;
import core.model.PlayerID;
import lombok.RequiredArgsConstructor;
import mudgame.server.state.ServerGameState;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class OnlyOnePlayerLeft implements GameOverCondition {
    private final EntityBoardView entityBoard;

    public OnlyOnePlayerLeft(ServerGameState gameState) {
        this.entityBoard = gameState.entityBoard();
    }

    @Override
    public boolean isGameOver() {
        return entityBoard
                       .allEntities()
                       .stream()
                       .map(Entity::owner)
                       .collect(Collectors.toSet())
                       .size() == 1;
    }

    @Override
    public Optional<List<PlayerID>> winners() {
        Set<PlayerID> players = entityBoard
                .allEntities()
                .stream()
                .map(Entity::owner)
                .collect(Collectors.toSet());

        if (players.size() == 1)
            return Optional.of(new ArrayList<>(players));
        else
            return Optional.empty();
    }

}
