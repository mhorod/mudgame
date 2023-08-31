package middleware.messages_to_client;

import middleware.Client;

import java.io.Serializable;

public interface MessageToClient extends Serializable {
    void execute(Client client);
}
