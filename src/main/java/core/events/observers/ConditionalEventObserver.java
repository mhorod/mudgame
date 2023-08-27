package core.events.observers;

import core.events.model.Event;
import core.model.PlayerID;

import java.util.function.Predicate;

public interface ConditionalEventObserver {
    void receive(Event event, Predicate<PlayerID> shouldPlayerReceive);
}
