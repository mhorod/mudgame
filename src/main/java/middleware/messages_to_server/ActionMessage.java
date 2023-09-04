package middleware.messages_to_server;

import middleware.model.UserID;
import middleware.server.GameServer;
import mudgame.events.Action;

public record ActionMessage(Action action) implements MessageToServer {
    @Override
    public void execute(GameServer server, UserID senderID) {
        server.processAction(action, senderID);
    }
}
