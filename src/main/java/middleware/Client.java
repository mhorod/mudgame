package middleware;

import core.client.ClientCore;
import core.client.ClientGameState;
import core.model.PlayerID;
import lombok.Getter;
import middleware.communicators.ClientSideCommunicator;

@Getter
public class Client {
    private final ClientSideCommunicator communicator;
    private ClientCore core;

    public Client(ClientSideCommunicator communicator) {
        this.communicator = communicator;
    }

    public void setState(ClientGameState state) {
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