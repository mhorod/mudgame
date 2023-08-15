package core.events;

import core.id.PlayerID;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class PlayerEventObserver implements EventObserver
{
    private final PlayerID playerID;
    private final EventObserver observer;

    @Override
    public void receive(Event event)
    {
        observer.receive(event);
    }

    public PlayerID getPlayerID()
    {
        return playerID;
    }
}
