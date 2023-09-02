package middleware.remote;

import core.client.ClientGameState;
import core.events.Event;
import middleware.Client;
import middleware.GameClient;
import middleware.communicators.MessageQueue;
import middleware.communicators.Sender;
import middleware.messages_to_client.MessageToClient;
import middleware.messages_to_server.MessageToServer;

import java.util.Optional;

public final class RemoteClient implements Client {
    private final Sender<MessageToServer> sender;
    private final MessageQueue<MessageToClient> messageQueue;
    private RemoteGameClient currentGameClient;
    private boolean coreChanged = false;

    public RemoteClient(Sender<MessageToServer> sender, MessageQueue<MessageToClient> messageQueue) {
        this.sender = sender;
        this.messageQueue = messageQueue;
    }

    public void processAllMessages() {
        while (messageQueue.hasMessage())
            messageQueue.removeMessage().execute(this);
    }

    public boolean hasCoreChanged() {
        boolean lastStatus = coreChanged;
        coreChanged = false;
        return lastStatus;
    }

    @Override
    public Optional<GameClient> getGameClient() {
        return Optional.ofNullable(currentGameClient);
    }

    public void sendMessage(MessageToServer message) {
        sender.sendMessage(message);
    }

    public void setGameState(ClientGameState state) {
        currentGameClient = new RemoteGameClient(this, state);
        coreChanged = true;
    }

    public void receiveEvent(Event event) {
        currentGameClient.registerEvent(event);
    }
}
