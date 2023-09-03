package middleware.remote;

import core.client.ClientGameState;
import core.events.Event;
import lombok.extern.slf4j.Slf4j;
import middleware.Client;
import middleware.GameClient;
import middleware.communication.*;
import middleware.messages_to_client.MessageToClient;
import middleware.messages_to_server.MessageToServer;
import middleware.network.ClientNetworkController;
import middleware.network.ConnectionStatus;

import java.util.Optional;

@Slf4j
public final class RemoteClient implements Client {
    private final ClientNetworkController controller;
    private final MessageQueue<MessageToClient> messageQueue;
    private RemoteGameClient currentGameClient;
    private boolean coreChanged = false;

    public RemoteClient() {
        AddableMessageQueue<MessageToClient> queue = new AddableMessageQueue<>();
        this.controller = new ClientNetworkController(queue);
        this.messageQueue = queue;
    }

    public void processAllMessages() {
        while (messageQueue.hasMessage())
            messageQueue.removeMessage().execute(this);
    }

    public boolean hasCoreChanged() {
        boolean status = coreChanged;
        coreChanged = false;
        return status;
    }

    @Override
    public Optional<GameClient> getGameClient() {
        return Optional.ofNullable(currentGameClient);
    }

    @Override
    public ConnectionStatus getNetworkStatus() {
        return controller.getStatus();
    }

    @Override
    public void disconnect() {
        controller.disconnect();
    }

    @Override
    public void connectAsynchronously(String host, int port) {
        controller.connectSocketAsynchronously(host, port);
    }

    public void sendMessage(MessageToServer message) {
        log.debug(message.toString());
        controller.sendMessage(message);
    }

    public void setGameState(ClientGameState state) {
        currentGameClient = new RemoteGameClient(state, this);
        coreChanged = true;
    }

    public void receiveEvent(Event event) {
        currentGameClient.registerEvent(event);
    }
}
