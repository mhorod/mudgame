package middleware.remote;

import core.client.ClientGameState;
import core.events.Event;
import lombok.extern.slf4j.Slf4j;
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

import java.net.Socket;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

@Slf4j
public final class RemoteNetworkClient implements NetworkClient<RemoteNetworkClient> {
    private static final Duration IDLE_TIMEOUT = Duration.ofSeconds(10);
    private static final Duration PING_TIMEOUT = Duration.ofSeconds(5);
    private static final Duration USED_ID_TIMEOUT = Duration.ofSeconds(5);
    // TODO remove GLOBAL_CLIENT
    public static RemoteNetworkClient GLOBAL_CLIENT = new RemoteNetworkClient();
    private final Queue<MessageToClient> messageQueue = new LinkedBlockingQueue<>();
    private Instant lastIncoming = Instant.EPOCH, lastPing = Instant.EPOCH, connectionBegan = Instant.EPOCH;

    private NetworkStatus networkStatus = NetworkStatus.DISCONNECTED;
    private Sender<MessageToServer> sender;
    private RemoteServerClient currentServerClient;

    private void verifyNetworkStatus() {
        if (networkStatus == NetworkStatus.OK) {
            if (sender.isClosed()) {
                clearStateAndSetStatus(NetworkStatus.FAILED);
                return;
            }
            if (Duration.between(lastIncoming, Instant.now()).compareTo(IDLE_TIMEOUT) < 0)
                return;
            if (lastPing.compareTo(lastIncoming) <= 0) {
                sendMessage(new PingToServer("pingFromClient", true));
                lastPing = Instant.now();
            } else if (Duration.between(lastPing, Instant.now()).compareTo(PING_TIMEOUT) > 0)
                clearStateAndSetStatus(NetworkStatus.FAILED);
        }
        if (networkStatus == NetworkStatus.ATTEMPTING) {
            if (Duration.between(connectionBegan, Instant.now()).compareTo(USED_ID_TIMEOUT) < 0)
                return;
            clearStateAndSetStatus(NetworkStatus.FAILED);
        }
    }

    private void clearStateAndSetStatus(NetworkStatus status) {
        if (sender != null) {
            sender.close();
            sender = null;
            currentServerClient = null;
        }
        networkStatus = status;
    }

    public void disconnect() {
        clearStateAndSetStatus(NetworkStatus.DISCONNECTED);
    }

    public void reportConnectionAttempt() {
        clearStateAndSetStatus(NetworkStatus.ATTEMPTING);
    }

    public void setSocketConnection(Socket socket) {
        if (networkStatus != NetworkStatus.ATTEMPTING)
            throw new RuntimeException("reportConnectionAttempt() should be called before setSocketConnection()");
        if (!socket.isConnected() || socket.isClosed())
            throw new RuntimeException("setSocketConnection() called with bad socket");

        connectionBegan = Instant.now();
        sender = new SocketSender<>(socket);
        new SocketReceiver<>(socket, MessageToClient.class, this::registerMessage);

        networkStatus = NetworkStatus.ATTEMPTING;
    }

    public void sendMessage(MessageToServer message) {
        if (networkStatus != NetworkStatus.OK)
            throw new RuntimeException("Attempting to send message using disconnected NetworkClient");
        log.info("[SND] " + message);
        sender.sendMessage(message);
    }

    private void registerMessage(MessageToClient message) {
        log.info("[REC] " + message);
        lastIncoming = Instant.now();
        messageQueue.add(message);
    }

    public void setUserID(UserID userID) {
        if (networkStatus != NetworkStatus.ATTEMPTING)
            throw new RuntimeException("setUsedId() should only be called when network status is ATTEMPTING");

        currentServerClient = new RemoteServerClient(userID, this);
        networkStatus = NetworkStatus.OK;
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

    public void setCurrentRoom(RoomInfo roomInfo) {
        Objects.requireNonNull(currentServerClient).setCurrentRoom(roomInfo);
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
        verifyNetworkStatus();
        while (!messageQueue.isEmpty())
            messageQueue.remove().execute(this);
    }
}
