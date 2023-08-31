package core.turns;

import core.events.Event;
import core.events.EventObserver;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public final class EventPlayerManager implements EventObserver {
    private final PlayerManager playerManager;
    private final EventObserver conditionalEventObserver;


    @Override
    public void receive(Event event) {
        if (event instanceof CompleteTurn) {
            playerManager.completeTurn();
            conditionalEventObserver.receive(event);
        }
    }
}
