package mudgame.server.actions;

import core.entities.model.Entity;
import core.entities.model.components.Attack;
import core.entities.model.components.Movement;
import core.resources.Resources;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mudgame.controls.actions.Action;
import mudgame.controls.actions.CompleteTurn;
import mudgame.controls.events.NextTurn;
import mudgame.controls.events.ProduceResources;
import mudgame.server.internal.InteractiveState;

@Slf4j
@RequiredArgsConstructor
final class CompleteTurnProcessor {
    private final InteractiveState state;
    private final EventSender sender;

    void process(Action action) {
        if (action instanceof CompleteTurn)
            completeTurn();
    }

    private void completeTurn() {
        state.completeTurn();
        NextTurn event = new NextTurn(state.currentPlayer());
        sender.sendToEveryone(event);

        Resources resources = state.produceResources(state.currentPlayer());
        state.playerEntities(state.currentPlayer()).forEach(this::newTurn);

        log.debug("Produced resources for player: {}, {}", state.currentPlayer(), resources);
        if (!resources.isEmpty())
            sender.send(new ProduceResources(resources), state.currentPlayer());
    }

    private void newTurn(Entity e) {
        e.getAttack().ifPresent(Attack::newTurn);
        e.getMovement().ifPresent(Movement::newTurn);
    }

}
