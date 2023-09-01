package middleware;

import core.client.ClientCore;
import core.client.ClientGameState;
import core.events.Event;
import core.model.PlayerID;
import middleware.communicators.MessageQueue;
import middleware.communicators.Sender;
import middleware.messages_to_client.MessageToClient;
import middleware.messages_to_server.MessageToServer;

import java.util.ArrayDeque;
import java.util.Objects;
import java.util.Optional;
import java.util.Queue;

public final class Client {
    private final Sender<MessageToServer> sender;
    private final MessageQueue<MessageToClient> messageQueue;
    private final Queue<Event> eventQueue = new ArrayDeque<>();
    private ClientCore core;
    private boolean coreChanged = false;

    public Client(Sender<MessageToServer> sender, MessageQueue<MessageToClient> messageQueue) {
        this.sender = sender;
        this.messageQueue = messageQueue;
    }

    public PlayerID myPlayerID() {
        return Objects.requireNonNull(core).state().playerID();
    }

    public void processAllMessages() {
        while (messageQueue.hasMessage())
            messageQueue.removeMessage().execute(this);
    }

    public void sendMessage(MessageToServer message) {
        sender.sendMessage(message);
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

    public Optional<ClientGameState> getGameState() {
        if (core == null)
            return Optional.empty();
        return Optional.of(core.state());
    }

    public void setGameState(ClientGameState state) {
        this.core = new ClientCore(state);
        eventQueue.clear();
        coreChanged = true;
    }
}
