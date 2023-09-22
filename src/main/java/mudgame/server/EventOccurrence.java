package mudgame.server;

import core.model.PlayerID;
import mudgame.controls.events.Event;

import java.io.Serializable;
import java.util.List;

public record EventOccurrence(Event event, List<PlayerID> recipients) implements Serializable {
    public static EventOccurrence of(Event event, PlayerID... players) {
        return new EventOccurrence(event, List.of(players));
    }
}
