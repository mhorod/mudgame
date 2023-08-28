package core.components;

import core.events.Event;
import core.model.PlayerID;

import java.util.function.Predicate;

public interface ConditionalEventObserver {
    void receive(Event event, Predicate<PlayerID> shouldPlayerReceive);
}
