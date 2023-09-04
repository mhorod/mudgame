package middleware;

import mudgame.events.Event.Action;
import middleware.communicators.MultiSender;
import middleware.communicators.ServerSideCommunicator;
import middleware.messages_to_client.MessageToClient;
import middleware.messages_to_server.MessageToServer;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class GameServer implements NotificationProcessor, MultiSender<MessageToClient> {
    private final Map<UserID, ServerSideCommunicator> communicatorMap = new HashMap<>();
    private final Map<UserID, Game> gameMap = new HashMap<>();

    @Override
    public synchronized void processNotification(UserID source) {
        ServerSideCommunicator communicator = communicatorMap.get(source);
        if (communicator == null)
            return;
        while (communicator.hasMessage()) {
            MessageToServer message = communicator.removeMessage();
            message.execute(this, source);
        }
    }

    public void addConnection(UserID userID, ServerSideCommunicator communicator) {
        if (communicatorMap.containsKey(userID))
            throw new IllegalArgumentException(userID + " is already connected");
        communicatorMap.put(userID, communicator);
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

    public void processAction(Action action, UserID userID) {
        Game game = gameMap.get(userID);
        if (game != null)
            game.process(action, userID);
    }
}
