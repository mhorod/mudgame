package middleware.messages_to_server;

import core.events.Action;
import middleware.server.GameServer;
import middleware.model.UserID;

public record ActionMessage(Action action) implements MessageToServer {
    @Override
    public void execute(GameServer server, UserID senderID) {
        server.processAction(action, senderID);
    }
}
