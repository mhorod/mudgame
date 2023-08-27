package core.events.observers;

import core.events.model.Event;

public interface EventObserver {
    void receive(Event event);
}
