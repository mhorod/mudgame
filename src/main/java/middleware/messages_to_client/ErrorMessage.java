package middleware.messages_to_client;

import middleware.Client;

public record ErrorMessage(String errorText) implements MessageToClient {
    @Override
    public void execute(Client client) {
        System.err.println(errorText);
    }
}
