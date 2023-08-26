package core.events.observers;

import core.events.model.EventOccurrence;

public interface EventOccurrenceObserver {
    void receive(EventOccurrence eventOccurrence);
}
