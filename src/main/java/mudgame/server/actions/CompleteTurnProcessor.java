package mudgame.server.actions;

import core.resources.Resources;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mudgame.controls.actions.Action;
import mudgame.controls.actions.CompleteTurn;
import mudgame.controls.events.Event;
import mudgame.controls.events.NextTurn;
import mudgame.controls.events.ProduceResources;
import mudgame.server.EventOccurrence;
import mudgame.server.internal.InteractiveState;

@Slf4j
@RequiredArgsConstructor
final class CompleteTurnProcessor {
    private final InteractiveState state;
    private final Sender sender;

    void process(Action action) {
        if (action instanceof CompleteTurn)
            completeTurn();
    }

    private void completeTurn() {
        state.completeTurn();
        NextTurn event = new NextTurn(state.currentPlayer());
        sender.send(seenByEveryone(event));

        Resources resources = state.produceResources(state.currentPlayer());
        log.debug("Produced resources for player: {}, {}", state.currentPlayer(), resources);
        if (!resources.isEmpty())
            sender.send(new ProduceResources(resources), state.currentPlayer());
    }

    private EventOccurrence seenByEveryone(Event event) {
        return new EventOccurrence(event, state.players());
    }

}
