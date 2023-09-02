package middleware.messages_to_client;

import middleware.remote.RemoteClient;

import java.io.Serializable;

public interface MessageToClient extends Serializable {
    void execute(RemoteClient client);
}
