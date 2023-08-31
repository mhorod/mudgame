package middleware.messages_to_server;

import core.events.Event.Action;
import middleware.GameServer;
import middleware.UserID;

public record ActionMessage(Action action) implements MessageToServer {
    @Override
    public void execute(GameServer server, UserID senderID) {
        server.processAction(action, senderID);
    }
}

// TODO tests for visible positions method in core
