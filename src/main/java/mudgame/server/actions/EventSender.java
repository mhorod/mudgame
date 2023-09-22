package mudgame.server.actions;

import core.model.PlayerID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mudgame.controls.events.Event;
import mudgame.server.EventOccurrence;
import mudgame.server.EventOccurrenceObserver;
import org.apache.commons.lang3.SerializationUtils;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
public final class EventSender {
    private final EventOccurrenceObserver eventOccurrenceObserver;
    private final List<PlayerID> players;

    public void send(List<EventOccurrence> eventOccurrences) {
        log.debug("Sending event occurrences {}", eventOccurrences);
        eventOccurrences.forEach(this::send);
    }

    public void send(EventOccurrence eventOccurrence) {
        log.debug("Sending event occurrence {}", eventOccurrence);
        eventOccurrenceObserver.receive(SerializationUtils.clone(eventOccurrence));
    }

    public void send(Event event, PlayerID player) {
        log.debug("Sending event {} to player {}", event, player);
        eventOccurrenceObserver.receive(SerializationUtils.clone(event), player);
    }

    public void sendToEveryone(Event event) {
        send(new EventOccurrence(event, players));
    }
}
