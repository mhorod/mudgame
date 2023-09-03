package middleware.remote;

import core.events.Action;
import middleware.communication.NotificationProcessor;
import middleware.communication.Sender;
import middleware.messages_to_client.MessageToClient;
import middleware.messages_to_server.MessageToServer;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class GameServer implements NotificationProcessor<MessageToServer> {
    private final Map<UserID, Sender<MessageToClient>> senderMap = new HashMap<>();
    private final Map<UserID, Game> gameMap = new HashMap<>();

    @Override
    public synchronized void processMessage(UserID source, MessageToServer message) {
        message.execute(this, source);
    }

    public void addConnection(UserID userID, Sender<MessageToClient> sender) {
        if (senderMap.containsKey(userID))
            throw new IllegalArgumentException(userID + " is already connected");
        senderMap.put(userID, sender);
    }

    public void sendMessage(UserID destination, MessageToClient message) {
        Sender<MessageToClient> sender = senderMap.get(destination);
        if (sender != null)
            sender.sendMessage(message);
    }

    public void startGame(List<UserID> usersToPlay) {
        if (usersToPlay.isEmpty())
            throw new IllegalArgumentException("You can't start the game without players");
        boolean someoneIsPlaying = usersToPlay.stream().anyMatch(gameMap::containsKey);
        if (someoneIsPlaying)
            throw new IllegalArgumentException("Someone is already playing");

        Game newGame = new Game(usersToPlay, this);
        for (UserID userID : usersToPlay)
            gameMap.put(userID, newGame);
    }

    public void processAction(Action action, UserID userID) {
        Game game = gameMap.get(userID);
        if (game != null)
            game.process(action, userID);
    }
}
