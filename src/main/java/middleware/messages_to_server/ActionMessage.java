package middleware.messages_to_server;

import core.events.Action;
import middleware.model.UserID;
import middleware.server.GameServer;

public record ActionMessage(Action action) implements MessageToServer {
    @Override
    public void execute(GameServer server, UserID senderID) {
        server.processAction(action, senderID);
    }
}
