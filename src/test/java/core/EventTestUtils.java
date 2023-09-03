package core;

import core.client.ClientCore;
import core.events.Event;
import core.events.Action;
import core.server.ServerCore;
import lombok.experimental.UtilityClass;

@UtilityClass
public class EventTestUtils {
    public static void send(ClientCore core, Event... events) {
        for (Event event : events)
            core.receive(event);
    }

    @SafeVarargs
    public static void process(ServerCore core, PlayerAction<? extends Action>... actions) {
        for (PlayerAction<? extends Action> action : actions)
            core.process(action.action(), action.actor());
    }
}
