package middleware.clients;

import core.client.ClientCore;
import core.client.ClientGameState;
import core.events.Event;
import core.model.PlayerID;

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
    public ClientGameState getGameState() {
        return core.state();
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
