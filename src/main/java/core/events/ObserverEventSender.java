package core.events;

import core.model.PlayerID;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public class ObserverEventSender implements EventSource, EventSender
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

    @Override
    public void send(Event event, Predicate<PlayerID> shouldPlayerReceive)
    {
        Predicate<PlayerEventObserver> shouldReceive = o -> shouldPlayerReceive.test(o.playerID());

        observers.forEach(o -> o.receive(event));
        playerObservers.stream().filter(shouldReceive).forEach(o -> o.receive(event));
    }
}
