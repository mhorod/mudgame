package core.server;

import core.events.EventOccurrence;

public interface EventOccurrenceObserver {
    void receive(EventOccurrence eventOccurrence);
}
