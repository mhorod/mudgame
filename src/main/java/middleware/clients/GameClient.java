package middleware.clients;

import core.event.Event;
import core.model.PlayerID;
import mudgame.client.MudClientCore;
import mudgame.controls.Controls;

import java.util.Optional;

public interface GameClient {
    PlayerID myPlayerID();

    MudClientCore getCore();

    boolean hasEvent();

    Optional<Event> peekEvent();

    void processEvent();

    Controls getControls();
}
