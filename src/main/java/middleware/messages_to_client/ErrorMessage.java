package middleware.messages_to_client;

import lombok.extern.slf4j.Slf4j;
import middleware.remote.RemoteClient;

@Slf4j
public record ErrorMessage(String errorText) implements MessageToClient {
    @Override
    public void execute(RemoteClient client) {
        log.error(errorText);
    }
}
