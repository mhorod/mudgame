package middleware.messages_to_client;

import middleware.remote.RemoteNetworkClient;
import mudgame.client.ClientGameState;

public record SetGameStateMessage(ClientGameState state) implements MessageToClient {
    @Override
    public void execute(RemoteNetworkClient client) {
        client.setGameState(state);
    }
}
