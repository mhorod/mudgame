package mudgame.events;

import core.model.PlayerID;

import java.util.List;

public interface EventOccurrenceObserver {
    void receive(EventOccurrence eventOccurrence);
    default void receive(Event event, PlayerID recipient) {
        receive(new EventOccurrence(event, List.of(recipient)));
    }
}
