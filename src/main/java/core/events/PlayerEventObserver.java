package core.events;

import core.model.PlayerID;

public record PlayerEventObserver(PlayerID playerID, EventObserver observer)
        implements EventObserver {
    @Override
    public void receive(Event event) {
        observer.receive(event);
    }
}
