package middleware.clients;

import core.event.Event;
import core.model.PlayerID;
import mudgame.client.ClientGameState;
import mudgame.client.MudClientCore;
import mudgame.controls.Controls;

import java.util.ArrayDeque;
import java.util.Optional;
import java.util.Queue;

public abstract class AbstractGameClient implements GameClient {
    private final Queue<Event> eventQueue = new ArrayDeque<>();
    private final MudClientCore core;

    public AbstractGameClient(ClientGameState state) {
        core = new MudClientCore(state);
    }

    @Override
    public PlayerID myPlayerID() {
        return core.state().playerID();
    }

    @Override
    public MudClientCore getCore() {
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

    public Controls getControls() {
        return new Controls(myPlayerID(), this::sendAction);
    }
}
