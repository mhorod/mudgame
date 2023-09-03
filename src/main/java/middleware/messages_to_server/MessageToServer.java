package middleware.messages_to_server;

import middleware.server.GameServer;
import middleware.model.UserID;

import java.io.Serializable;

public interface MessageToServer extends Serializable {
    void execute(GameServer server, UserID senderID);
}
