package middleware.messages_to_client;

import core.client.ClientGameState;
import middleware.Client;

public record GameStartedMessage(ClientGameState state) implements MessageToClient {
    @Override
    public void execute(Client client) {
        client.setState(state);
    }
}
