package middleware.messages_to_server;

import mudgame.events.Event.Action;
import middleware.GameServer;
import middleware.UserID;

public record ActionMessage(Action action) implements MessageToServer {
    @Override
    public void execute(GameServer server, UserID senderID) {
        server.processAction(action, senderID);
    }
}
