package middleware.clients;

import core.model.PlayerID;
import mudgame.client.ClientCore;
import mudgame.events.Action;
import mudgame.events.Event;

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
