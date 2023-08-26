package core.events.senders;

import core.events.model.Event;
import core.events.observers.ConditionalEventObserver;
import core.events.observers.EventObserver;
import core.events.model.EventSource;
import core.events.model.PlayerEventObserver;
import core.model.PlayerID;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

@RequiredArgsConstructor
public class ConditionalEventSender implements EventSource, ConditionalEventObserver {
    private final List<EventObserver> observers = new ArrayList<>();
    private final List<PlayerEventObserver> playerObservers = new ArrayList<>();

    @Override
    public void addObserver(EventObserver observer) {
        observers.add(observer);
    }

    @Override
    public void addObserver(PlayerEventObserver observer) {
        playerObservers.add(observer);
    }

    @Override
    public void receive(Event event, Predicate<PlayerID> shouldPlayerReceive) {
        observers.forEach(o -> o.receive(event));
        playerObservers.stream()
                .filter(o -> shouldPlayerReceive.test(o.playerID()))
                .forEach(o -> o.receive(event));
    }

}
