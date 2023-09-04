package core;

import mudgame.client.ClientCore;
import mudgame.events.Event;
import mudgame.events.Event.Action;
import mudgame.server.ServerCore;
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
