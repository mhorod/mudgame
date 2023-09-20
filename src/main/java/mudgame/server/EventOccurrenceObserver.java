package mudgame.server;

import core.model.PlayerID;
import mudgame.controls.events.Event;

import java.util.List;

public interface EventOccurrenceObserver {
    void receive(EventOccurrence eventOccurrence);
    default void receive(Event event, PlayerID recipient) {
        receive(new EventOccurrence(event, List.of(recipient)));
    }
}
