package core.events.model;

import core.events.observers.EventObserver;
import core.model.PlayerID;

public record PlayerEventObserver(PlayerID playerID, EventObserver observer)
        implements EventObserver {
    @Override
    public void receive(Event event) {
        observer.receive(event);
    }
}
