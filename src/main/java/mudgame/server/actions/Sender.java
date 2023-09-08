package mudgame.server.actions;

import core.event.Event;
import core.event.EventOccurrence;
import core.model.PlayerID;
import lombok.RequiredArgsConstructor;
import mudgame.events.EventOccurrenceObserver;

import java.util.List;

@RequiredArgsConstructor
public final class Sender {
    private final EventOccurrenceObserver eventOccurrenceObserver;

    public void send(List<EventOccurrence> eventOccurrences) {
        eventOccurrences.forEach(this::send);
    }

    public void send(EventOccurrence eventOccurrence) {
        eventOccurrenceObserver.receive(eventOccurrence);
    }

    public void send(Event event, PlayerID player) {
        eventOccurrenceObserver.receive(event, player);
    }
}
