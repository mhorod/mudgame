package middleware.messages_to_client;

import core.client.ClientGameState;
import middleware.remote.RemoteClient;

public record GameStartedMessage(ClientGameState state) implements MessageToClient {
    @Override
    public void execute(RemoteClient client) {
        client.setGameState(state);
    }
}
