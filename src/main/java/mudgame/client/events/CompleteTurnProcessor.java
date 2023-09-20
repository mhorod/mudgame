package mudgame.client.events;

import core.entities.model.Entity;
import core.entities.model.components.Movement;
import lombok.RequiredArgsConstructor;
import mudgame.client.ClientGameState;
import mudgame.controls.events.NextTurn;

import java.util.Optional;

@RequiredArgsConstructor
class CompleteTurnProcessor {
    private final ClientGameState state;

    void nextTurn(NextTurn e) {
        state.turnManager().nextTurn(e.currentPlayer());
        if (e.currentPlayer().equals(state.playerID()))
            state.entityBoard().playerEntities(state.playerID())
                    .stream()
                    .map(Entity::getMovement)
                    .flatMap(Optional::stream)
                    .forEach(Movement::newTurn);
    }
}
