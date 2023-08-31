package middleware;

import core.client.ClientCore;
import core.client.ClientGameState;
import core.events.Event;
import core.model.PlayerID;
import lombok.Getter;
import middleware.communicators.ClientSideCommunicator;

import java.util.ArrayDeque;
import java.util.Objects;
import java.util.Optional;
import java.util.Queue;

public final class Client {
    @Getter
    private final ClientSideCommunicator communicator;
    private final Queue<Event> eventQueue = new ArrayDeque<>();
    @Getter
    private ClientCore core;
    private boolean coreChanged = false;

    public Client(ClientSideCommunicator communicator) {
        this.communicator = communicator;
    }

    public PlayerID myPlayerID() {
        return Objects.requireNonNull(core).state().playerID();
    }

    public void processAllMessages() {
        while (communicator.hasMessage())
            communicator.removeMessage().execute(this);
    }

    public boolean hasCoreChanged() {
        boolean lastStatus = coreChanged;
        coreChanged = false;
        return lastStatus;
    }

    public Optional<Event> peekEvent() {
        return Optional.ofNullable(eventQueue.peek());
    }

    public void processEvent() {
        Event event = eventQueue.remove();
        Objects.requireNonNull(core).receive(event);
    }

    public void receiveEvent(Event event) {
        eventQueue.add(event);
    }

    public void setGameState(ClientGameState state) {
        this.core = new ClientCore(state);
        eventQueue.clear();
        coreChanged = true;
    }
}
