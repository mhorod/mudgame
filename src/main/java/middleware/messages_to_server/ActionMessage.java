package middleware.messages_to_server;

import core.events.Event.Action;
import middleware.Game;
import middleware.SimpleServer;
import middleware.UserID;

public record ActionMessage(Action action) implements MessageToServer {
    @Override
    public void execute(SimpleServer server, UserID senderID) {
        Game game = server.getGame(senderID);
        if (game != null)
            game.process(action, senderID);
    }
}
