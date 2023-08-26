package core.events.model;

import core.events.observers.EventObserver;

public interface EventSource {
    void addObserver(EventObserver observer);
    void addObserver(PlayerEventObserver observer);
}
