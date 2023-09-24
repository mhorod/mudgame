package mudgame.client.events;

import core.entities.model.Entity;
import core.entities.model.components.Attack;
import core.entities.model.components.Movement;
import lombok.RequiredArgsConstructor;
import mudgame.client.ClientGameState;
import mudgame.controls.events.NextTurn;

@RequiredArgsConstructor
class CompleteTurnProcessor {
    private final ClientGameState state;

    void nextTurn(NextTurn e) {
        state.turnManager().nextTurn(e.currentPlayer());
        if (e.currentPlayer().equals(state.playerID()))
            state.entityBoard().playerEntities(state.playerID()).forEach(this::newTurn);
    }

    private void newTurn(Entity e) {
        e.getAttack().ifPresent(Attack::newTurn);
        e.getMovement().ifPresent(Movement::newTurn);
    }
}
