package middleware.messages_to_client;

import mudgame.client.ClientGameState;
import middleware.Client;

public record GameStartedMessage(ClientGameState state) implements MessageToClient {
    @Override
    public void execute(Client client) {
        client.setGameState(state);
    }
}
