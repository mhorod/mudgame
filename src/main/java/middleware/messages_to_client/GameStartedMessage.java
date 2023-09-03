package middleware.messages_to_client;

import core.client.ClientGameState;
import middleware.remote.RemoteNetworkClient;

public record GameStartedMessage(ClientGameState state) implements MessageToClient {
    @Override
    public void execute(RemoteNetworkClient client) {
        client.setGameState(state);
    }
}
