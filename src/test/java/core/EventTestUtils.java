package core;

import lombok.experimental.UtilityClass;
import mudgame.client.ClientCore;
import mudgame.events.Action;
import mudgame.events.Event;
import mudgame.server.ServerCore;

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
