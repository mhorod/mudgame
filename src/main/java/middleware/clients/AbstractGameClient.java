package middleware.clients;

import core.model.PlayerID;
import mudgame.client.ClientCore;
import mudgame.client.ClientGameState;
import mudgame.events.Event;

import java.util.ArrayDeque;
import java.util.Optional;
import java.util.Queue;

public abstract class AbstractGameClient implements GameClient {
    private final Queue<Event> eventQueue = new ArrayDeque<>();
    private final ClientCore core;

    public AbstractGameClient(ClientGameState state) {
        core = new ClientCore(state);
    }

    @Override
    public PlayerID myPlayerID() {
        return core.state().playerID();
    }

    @Override
    public ClientCore getCore() {
        return core;
    }

    @Override
    public Optional<Event> peekEvent() {
        return Optional.ofNullable(eventQueue.peek());
    }

    @Override
    public void processEvent() {
        Event event = eventQueue.remove();
        core.receive(event);
    }

    public void registerEvent(Event event) {
        eventQueue.add(event);
    }
}
