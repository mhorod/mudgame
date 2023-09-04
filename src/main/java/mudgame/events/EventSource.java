package mudgame.events;

public interface EventSource {
    void addObserver(EventObserver observer);
    void addObserver(PlayerEventObserver observer);
}
