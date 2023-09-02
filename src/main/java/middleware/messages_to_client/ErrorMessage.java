package middleware.messages_to_client;

import lombok.extern.slf4j.Slf4j;
import middleware.Client;

@Slf4j
public record ErrorMessage(String errorText) implements MessageToClient {
    @Override
    public void execute(Client client) {
        log.error(errorText);
    }
}
