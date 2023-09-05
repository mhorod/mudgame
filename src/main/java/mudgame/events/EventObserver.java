package mudgame.events;


import core.event.Event;

public interface EventObserver {
    void receive(Event event);
}
