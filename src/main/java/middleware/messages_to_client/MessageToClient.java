package middleware.messages_to_client;

import middleware.remote.RemoteNetworkClient;

import java.io.Serializable;

public interface MessageToClient extends Serializable {
    void execute(RemoteNetworkClient client);
}
