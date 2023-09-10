package middleware.clients;

import core.event.Event;
import core.model.PlayerID;
import mudgame.client.MudClientCoreView;
import mudgame.controls.Controls;

import java.util.Optional;

public interface GameClient {
    PlayerID myPlayerID();

    MudClientCoreView getCore();

    boolean hasEvent();

    Optional<Event> peekEvent();

    void processEvent();

    Controls getControls();
}
