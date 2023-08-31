package middleware;

import core.client.ClientCore;
import core.client.ClientGameState;
import core.events.Event;
import core.model.PlayerID;
import lombok.Getter;
import middleware.communicators.ClientSideCommunicator;
import middleware.communicators.MessageQueueImpl;

@Getter
public final class Client {
    private final ClientSideCommunicator communicator;
    private final MessageQueueImpl<Event> events = new MessageQueueImpl<>();
    private ClientCore core;

    public Client(ClientSideCommunicator communicator) {
        this.communicator = communicator;
    }

    public void setGameState(ClientGameState state) {
        this.core = new ClientCore(state);
    }

    public PlayerID myPlayerID() {
        if (core == null)
            return null;
        return core.state().playerID();
    }

    public void processAllMessages() {
        while (communicator.hasMessage())
            communicator.removeMessage().execute(this);
    }
}
