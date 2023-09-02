package core.events;

public interface EventOccurrenceObserver {
    void receive(EventOccurrence eventOccurrence);
}
