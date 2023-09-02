package middleware.messages_to_server;

import middleware.remote.GameServer;
import middleware.remote.UserID;

import java.io.Serializable;

public interface MessageToServer extends Serializable {
    void execute(GameServer server, UserID senderID);
}
