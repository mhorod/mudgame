package middleware.clients;

import core.client.ClientCore;
import core.events.Action;
import core.events.Event;
import core.model.PlayerID;

import java.util.Optional;

public interface GameClient {
    PlayerID myPlayerID();

    ClientCore getCore();

    Optional<Event> peekEvent();

    void processEvent();

    void sendAction(Action action);

    // TODO controls
    // Controls getControls();
}
