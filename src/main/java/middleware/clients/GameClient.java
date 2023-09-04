package middleware.clients;

import core.event.Action;
import core.event.Event;
import core.model.PlayerID;
import mudgame.client.MudClientCore;

import java.util.Optional;

public interface GameClient {
    PlayerID myPlayerID();

    MudClientCore getCore();

    Optional<Event> peekEvent();

    void processEvent();

    void sendAction(Action action);

    // TODO controls
    // Controls getControls();
}
