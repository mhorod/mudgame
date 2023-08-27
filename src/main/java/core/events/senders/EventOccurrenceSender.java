package core.events.senders;

import core.events.observers.EventObserver;
import core.events.model.EventOccurrence;
import core.events.model.EventSource;
import core.events.model.PlayerEventObserver;
import core.events.observers.EventOccurrenceObserver;

import java.util.ArrayList;
import java.util.List;

public class EventOccurrenceSender implements EventSource, EventOccurrenceObserver {
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
    public void receive(EventOccurrence eventOccurrence) {
        observers.forEach(o -> o.receive(eventOccurrence.event()));
        playerObservers.stream()
                .filter(o -> eventOccurrence.recipients().contains(o.playerID()))
                .forEach(o -> o.receive(eventOccurrence.event()));
    }
}