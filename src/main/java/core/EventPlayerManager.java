package core;

import core.events.Event;
import core.events.EventObserver;
import core.events.EventSender;
import core.turns.CompleteTurn;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class EventPlayerManager implements EventObserver
{
    private final PlayerManager playerManager;
    private final EventSender eventSender;


    @Override
    public void receive(Event event)
    {
        if (event instanceof CompleteTurn)
        {
            playerManager.completeTurn();
            eventSender.send(event, player -> true);
        }
    }
}
