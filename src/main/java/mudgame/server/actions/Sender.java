package mudgame.server.actions;

import core.event.Event;
import core.event.EventOccurrence;
import core.model.PlayerID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mudgame.events.EventOccurrenceObserver;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
public final class Sender {
    private final EventOccurrenceObserver eventOccurrenceObserver;

    public void send(List<EventOccurrence> eventOccurrences) {
        log.debug("Sending event occurrences {}", eventOccurrences);
        eventOccurrences.forEach(this::send);
    }

    public void send(EventOccurrence eventOccurrence) {
        log.debug("Sending event occurrence {}", eventOccurrence);
        eventOccurrenceObserver.receive(eventOccurrence);
    }

    public void send(Event event, PlayerID player) {
        log.debug("Sending event {} to player {}", event, player);
        eventOccurrenceObserver.receive(event, player);
    }
}
