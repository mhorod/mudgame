package mudgame.events;

import core.model.PlayerID;

import java.util.function.Predicate;

public interface ConditionalEventObserver {
    void receive(Event event, Predicate<PlayerID> shouldPlayerReceive);
}
