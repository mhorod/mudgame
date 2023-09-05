package middleware.messages_to_client;

import lombok.extern.slf4j.Slf4j;
import middleware.remote.RemoteNetworkClient;

@Slf4j
public record ErrorMessage(String errorText) implements MessageToClient {
    @Override
    public void execute(RemoteNetworkClient client) {
        log.error(errorText);
    }
}
