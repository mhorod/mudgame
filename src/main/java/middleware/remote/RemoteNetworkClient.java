package middleware.remote;

import middleware.clients.Connection;
import middleware.clients.NetworkClient;
import middleware.clients.ServerClient;
import middleware.communication.Sender;
import middleware.communication.SocketReceiver;
import middleware.communication.SocketSender;
import middleware.messages_to_client.MessageToClient;
import middleware.messages_to_server.MessageToServer;
import middleware.messages_to_server.PingToServer;
import middleware.model.RoomInfo;
import middleware.model.UserID;
import mudgame.client.ClientGameState;
import mudgame.events.Event;

import java.io.Closeable;
import java.io.IOException;
import java.net.Socket;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

public final class RemoteNetworkClient implements NetworkClient<RemoteNetworkClient> {
    private static final Duration IDLE_TIMEOUT = Duration.ofSeconds(30);
    private static final Duration PING_TIMEOUT = Duration.ofSeconds(5);
    private static final Duration USED_ID_TIMEOUT = Duration.ofSeconds(5);

    private final Queue<MessageToClient> messageQueue = new LinkedBlockingQueue<>();
    private Instant lastIncoming = Instant.EPOCH, lastPing = Instant.EPOCH, connectionBegan = Instant.EPOCH;

    private NetworkStatus networkStatus = NetworkStatus.DISCONNECTED;
    private Closeable toClose;
    private Sender<MessageToServer> sender;
    private RemoteServerClient currentServerClient;

    private void verifyNetworkStatus() {
        if (networkStatus == NetworkStatus.OK) {
            if (Duration.between(lastIncoming, Instant.now()).compareTo(IDLE_TIMEOUT) < 0)
                return;
            if (lastPing.compareTo(lastIncoming) <= 0) {
                sendMessage(new PingToServer("ping", true));
                lastPing = Instant.now();
            } else if (Duration.between(lastPing, Instant.now()).compareTo(PING_TIMEOUT) > 0)
                disconnect();
        }
        if (networkStatus == NetworkStatus.ATTEMPTING) {
            if (Duration.between(connectionBegan, Instant.now()).compareTo(USED_ID_TIMEOUT) < 0)
                return;
            disconnect();
            networkStatus = NetworkStatus.FAILED;
        }
    }

    public void disconnect() {
        if (toClose != null) {
            try {
                toClose.close();
            } catch (IOException ignored) {
            }
            toClose = null;
            sender = null;
            currentServerClient = null;
        }
        networkStatus = NetworkStatus.DISCONNECTED;
    }

    public void reportConnectionAttempt() {
        disconnect();
        networkStatus = NetworkStatus.ATTEMPTING;
    }

    public void sendMessage(MessageToServer message) {
        if (networkStatus != NetworkStatus.OK)
            throw new RuntimeException(
                    "Attempting to send message using disconnected NetworkClient");
        sender.sendMessage(message);
    }

    public void setSocketConnection(Socket socket) {
        if (networkStatus != NetworkStatus.ATTEMPTING)
            throw new RuntimeException(
                    "reportConnectionAttempt() should be called before setSocketConnection()");
        if (!socket.isConnected() || socket.isClosed())
            throw new RuntimeException("setSocketConnection() called with bad socket");

        connectionBegan = Instant.now();
        toClose = socket;
        sender = new SocketSender<>(socket);
        new SocketReceiver<>(socket, MessageToClient.class, this::registerIncomingMessage);

        networkStatus = NetworkStatus.ATTEMPTING;
    }

    public void setUserID(UserID userID) {
        if (networkStatus != NetworkStatus.ATTEMPTING)
            throw new RuntimeException(
                    "setUsedId() should only be called when network status is ATTEMPTING");

        currentServerClient = new RemoteServerClient(userID, this);
        networkStatus = NetworkStatus.OK;
    }

    private void registerIncomingMessage(MessageToClient message) {
        lastIncoming = Instant.now();
        messageQueue.add(message);
    }

    public void registerEvent(Event event) {
        Objects.requireNonNull(currentServerClient).registerEvent(event);
    }

    public void setGameState(ClientGameState state) {
        Objects.requireNonNull(currentServerClient).setGameState(state);
    }


    public void setRoomList(List<RoomInfo> roomList) {
        Objects.requireNonNull(currentServerClient).setRoomList(roomList);
    }

    @Override
    public NetworkStatus getNetworkStatus() {
        verifyNetworkStatus();
        return networkStatus;
    }

    @Override
    public void connect(Connection<RemoteNetworkClient> connection) {
        connection.connect(this);
    }

    @Override
    public Optional<ServerClient> getServerClient() {
        return Optional.ofNullable(currentServerClient);
    }

    @Override
    public void processAllMessages() {
        while (!messageQueue.isEmpty())
            messageQueue.remove().execute(this);
    }
}
