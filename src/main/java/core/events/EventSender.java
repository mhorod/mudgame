package core.events;

import core.id.PlayerID;

import java.util.function.Predicate;

public interface EventSender
{
    void send(Event event, Predicate<PlayerID> shouldPlayerReceive);
}
