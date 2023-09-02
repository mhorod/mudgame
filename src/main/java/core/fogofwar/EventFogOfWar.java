package core.fogofwar;

import core.events.ConditionalEventObserver;
import core.events.EventObserver;
import core.events.EventOccurrence;
import core.events.EventOccurrenceObserver;
import core.model.PlayerID;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

import static java.util.stream.Collectors.toMap;

@Slf4j
public final class EventFogOfWar implements EventOccurrenceObserver {

    private final FogOfWar fow;
    private final ConditionalEventObserver eventObserver;
    private final Map<PlayerID, EventPlayerFogOfWar> eventFows;


    public EventFogOfWar(FogOfWar fow, ConditionalEventObserver eventObserver) {
        this.fow = fow;
        this.eventObserver = eventObserver;
        eventFows = fow.players()
                .stream()
                .collect(toMap(p -> p, this::mapToPlayerFow));
    }

    private EventPlayerFogOfWar mapToPlayerFow(PlayerID id) {
        return new EventPlayerFogOfWar(
                fow.playerFogOfWar(id),
                sendOnlyTo(id)
        );
    }

    private EventObserver sendOnlyTo(PlayerID id) {
        return e -> eventObserver.receive(e, i -> i == id);
    }

    @Override
    public void receive(EventOccurrence eventOccurrence) {
        log.info("Received event occurrence: {}", eventOccurrence);
        for (PlayerID p : eventOccurrence.recipients())
            eventFows.get(p).receive(eventOccurrence.event());
    }
}
