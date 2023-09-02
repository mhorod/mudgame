package core.fogofwar;

import core.events.ConditionalEventObserver;
import core.events.Event;
import core.events.EventObserver;
import core.model.PlayerID;

import java.util.List;

public final class EventFogOfWar implements EventObserver {

    private final FogOfWar fow;
    private final ConditionalEventObserver eventObserver;
    private final List<EventPlayerFogOfWar> eventFows;


    public EventFogOfWar(FogOfWar fow, ConditionalEventObserver eventObserver) {
        this.fow = fow;
        this.eventObserver = eventObserver;
        eventFows = fow.players()
                .stream()
                .map(this::mapToPlayerFow)
                .toList();
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


    public void receive(Event event) {
        eventFows.forEach(f -> f.receive(event));
    }

}
