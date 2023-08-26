package core;

import core.events.observers.ConditionalEventObserver;
import core.events.model.Event;
import core.events.observers.EventObserver;
import core.turns.CompleteTurn;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class EventPlayerManager implements EventObserver {
    private final PlayerManager playerManager;
    private final ConditionalEventObserver conditionalEventObserver;


    @Override
    public void receive(Event event) {
        if (event instanceof CompleteTurn) {
            playerManager.completeTurn();
            conditionalEventObserver.receive(event, player -> true);
        }
    }
}
