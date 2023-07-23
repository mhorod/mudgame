package core.events;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class EventLog implements EventObserver
{

    @Override
    public void receive(Event event)
    {
        log.info(event.toString());
    }
}
