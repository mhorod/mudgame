package core.events;

import core.id.PlayerID;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public class EventSender implements EventSource
{
    private final List<EventObserver> observers = new ArrayList<>();
    private final List<PlayerEventObserver> playerObservers = new ArrayList<>();

    @Override
    public void addObserver(EventObserver observer)
    {
        observers.add(observer);
    }

    @Override
    public void addObserver(PlayerEventObserver observer)
    {
        playerObservers.add(observer);
    }

    public void send(Event event, Predicate<PlayerID> shouldPlayerReceive)
    {
        Predicate<PlayerEventObserver> shouldReceive = o -> shouldPlayerReceive.test(
                o.getPlayerID());

        observers.forEach(o -> o.receive(event));
        playerObservers.stream().filter(shouldReceive).forEach(o -> o.receive(event));
    }
}
