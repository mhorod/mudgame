package middleware.messages_to_server;

import middleware.SimpleServer;
import middleware.UserID;

import java.io.Serializable;

public interface MessageToServer extends Serializable {
    void execute(SimpleServer server, UserID senderID);
}
