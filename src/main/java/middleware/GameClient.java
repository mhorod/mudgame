package middleware;

import core.client.ClientGameState;
import core.events.Action;
import core.events.Event;
import core.model.PlayerID;

import java.util.Optional;

public interface GameClient {
    PlayerID myPlayerID();

    ClientGameState getGameState();

    Optional<Event> peekEvent();

    void processEvent();

    void sendAction(Action action);
}
