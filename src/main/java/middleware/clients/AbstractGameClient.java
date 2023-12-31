package middleware.clients;

import core.model.PlayerID;
import mudgame.client.ClientGameState;
import mudgame.client.MudClientCore;
import mudgame.client.MudClientCoreView;
import mudgame.controls.Controls;
import mudgame.controls.actions.Action;
import mudgame.controls.events.Event;

import java.util.ArrayDeque;
import java.util.Optional;
import java.util.Queue;

public abstract class AbstractGameClient implements GameClient {
    private final Queue<Event> eventQueue = new ArrayDeque<>();
    private final MudClientCore core;

    protected AbstractGameClient(ClientGameState state) {
        core = new MudClientCore(state);
    }

    @Override
    public PlayerID myPlayerID() {
        return core.myPlayerID();
    }

    @Override
    public MudClientCoreView getCore() {
        return core;
    }

    @Override
    public Optional<Event> peekEvent() {
        return Optional.ofNullable(eventQueue.peek());
    }

    public boolean hasEvent() {
        return !eventQueue.isEmpty();
    }

    @Override
    public void processEvent() {
        Event event = eventQueue.remove();
        core.receive(event);
    }

    public void registerEvent(Event event) {
        eventQueue.add(event);
    }

    protected abstract void sendAction(Action action);

    public Controls getControls() {
        return new Controls(myPlayerID(), this::sendAction);
    }
}
