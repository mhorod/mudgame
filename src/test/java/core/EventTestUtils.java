package core;

import core.events.Event;
import core.events.Event.Action;
import lombok.experimental.UtilityClass;

@UtilityClass
public class EventTestUtils
{
    public static void send(GameCore core, Event... events)
    {
        for (Event event : events)
            core.receive(event);
    }

    @SafeVarargs
    public static void process(GameCore core, PlayerAction<? extends Action>... actions)
    {
        for (PlayerAction<? extends Action> action : actions)
            core.process(action.action(), action.actor());
    }
}
