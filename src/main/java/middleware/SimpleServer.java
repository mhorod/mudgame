package middleware;

import middleware.communicators.MultiSender;
import middleware.communicators.ServerSideCommunicator;
import middleware.messages_to_client.MessageToClient;
import middleware.messages_to_server.MessageToServer;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class SimpleServer implements NotificationProcessor, MultiSender<MessageToClient> {
    protected final Map<UserID, ServerSideCommunicator> communicatorMap = new HashMap<>();
    private final Map<UserID, Game> gameMap = new HashMap<>();

    @Override
    public void processNotification(UserID source) {
        ServerSideCommunicator communicator = communicatorMap.get(source);
        if (communicator == null)
            return;
        while (communicator.hasMessage()) {
            MessageToServer message = communicator.removeMessage();
            message.execute(this, source);
        }
    }

    @Override
    public void sendMessage(UserID destination, MessageToClient message) {
        ServerSideCommunicator communicator = communicatorMap.get(destination);
        if (communicator != null)
            communicator.sendMessage(message);
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

    public Game getGame(UserID userID) {
        return gameMap.get(userID);
    }
}
