package core;

import core.event.Action;
import core.event.Event;
import lombok.experimental.UtilityClass;
import mudgame.client.MudClientCore;
import mudgame.server.MudServerCore;

@UtilityClass
public class EventTestUtils {
    public static void send(MudClientCore core, Event... events) {
        for (Event event : events)
            core.receive(event);
    }

    @SafeVarargs
    public static void process(MudServerCore core, PlayerAction<? extends Action>... actions) {
        for (PlayerAction<? extends Action> action : actions)
            core.process(action.action(), action.actor());
    }
}
