package middleware.remote;

import core.event.Event;
import lombok.extern.slf4j.Slf4j;
import middleware.clients.Connection;
import middleware.clients.NetworkClient;
import middleware.clients.ServerClient;
import middleware.communication.NetworkDevice;
import middleware.communication.SocketReceiver;
import middleware.communication.SocketSender;
import middleware.messages_to_client.MessageToClient;
import middleware.messages_to_client.MessageToClientHandler;
import middleware.messages_to_server.MessageToServer;
import middleware.messages_to_server.MessageToServerFactory;
import middleware.messages_to_server.MessageToServerHandler;
import middleware.model.RoomInfo;
import middleware.model.UserID;
import mudgame.client.ClientGameState;

import java.net.Socket;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.function.Consumer;

@Slf4j
public final class RemoteNetworkClient implements NetworkClient<RemoteNetworkClient> {
    static final Duration SOCKET_CONNECTION_TIMEOUT = Duration.ofSeconds(10);
    static final Duration PING_TIMEOUT = Duration.ofSeconds(5);
    static final Duration PING_AFTER_IDLE = Duration.ofSeconds(15);
    static final Duration USER_ID_TIMEOUT = Duration.ofSeconds(5);

    // TODO remove GLOBAL_CLIENT
    public static NetworkClient<RemoteNetworkClient> GLOBAL_CLIENT = new RemoteNetworkClient();

    private final Queue<MessageToClient> messageQueue = new LinkedBlockingQueue<>();

    private NetworkStatus networkStatus = NetworkStatus.DISCONNECTED;
    private NetworkDevice networkDevice;
    private MessageToServerHandler sender;
    private RemoteServerClient currentServerClient;

    private Instant lastIncoming = Instant.EPOCH;
    private Instant lastPing = Instant.EPOCH;
    private Instant connectionBegan = Instant.EPOCH;

    private void verifyNetworkStatus() {
        if (networkStatus == NetworkStatus.OK) {
            if (networkDevice.isClosed()) {
                clearStateAndSetStatus(NetworkStatus.FAILED);
                return;
            }
            if (Duration.between(lastIncoming, Instant.now()).compareTo(PING_AFTER_IDLE) < 0)
                return;
            if (lastPing.compareTo(lastIncoming) <= 0) {
                sender.pingToServer();
                lastPing = Instant.now();
            } else if (Duration.between(lastPing, Instant.now()).compareTo(PING_TIMEOUT) > 0)
                clearStateAndSetStatus(NetworkStatus.FAILED);
        }
        if (networkStatus == NetworkStatus.ATTEMPTING && networkDevice != null) {
            if (Duration.between(connectionBegan, Instant.now()).compareTo(USER_ID_TIMEOUT) > 0)
                clearStateAndSetStatus(NetworkStatus.FAILED);
        }
    }

    private void clearStateAndSetStatus(NetworkStatus status) {
        if (networkDevice != null) {
            networkDevice.close();
            networkDevice = null;
            sender = null;
            currentServerClient = null;
        }
        networkStatus = status;
    }

    public void disconnect() {
        sender.disconnect();
        networkDevice.scheduleToClose();
        log.info("disconnect() called");
    }

    public void reportConnectionAttempt() {
        clearStateAndSetStatus(NetworkStatus.ATTEMPTING);
        log.info("reportConnectionAttempt() called, status is now ATTEMPTING");
    }

    public void setConnection(Consumer<MessageToServer> consumer, NetworkDevice device) {
        if (networkStatus != NetworkStatus.ATTEMPTING)
            throw new RuntimeException("reportConnectionAttempt() should be called before setSocketConnection()");
        if (device.isClosedOrScheduledToClose()) {
            networkStatus = NetworkStatus.FAILED;
            return;
        }

        sender = new MessageToServerFactory(consumer.andThen(
                message -> {
                    if (networkStatus != NetworkStatus.OK)
                        throw new RuntimeException("Attempting to send message using disconnected NetworkClient");
                    log.debug("[SND]: " + message);
                }
        ));
        networkDevice = device;

        connectionBegan = Instant.now();
        log.info("setConnection() is successful, status is now ATTEMPTING");
        networkStatus = NetworkStatus.ATTEMPTING;
    }

    public void setSocketConnection(Socket socket) {
        log.info("setSocketConnection() called, socket is closed: %b, is connected: %b "
                .formatted(socket.isClosed(), socket.isConnected()));

        SocketSender<MessageToServer> socketSender = new SocketSender<>(socket);
        new SocketReceiver<>(messageQueue::add, socket, MessageToClient.class);
        setConnection(socketSender::sendMessage, socketSender.getClosingDevice());
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
        while (!messageQueue.isEmpty()) {
            MessageToClient message = messageQueue.remove();
            log.info("[REC] " + message);
            lastIncoming = Instant.now();

            message.execute(messageToClientHandler);
        }
    }

    public MessageToServerHandler getSender() {
        return sender;
    }

    private final MessageToClientHandler messageToClientHandler = new MessageToClientHandler() {
        @Override
        public void error(String errorText) {
            // TODO somehow display it?
            log.warn(errorText);
        }

        @Override
        public void pingToClient() {
            sender.pongToServer();
        }

        @Override
        public void pongToClient() {
            // noop
        }

        @Override
        public void registerEvent(Event event) {
            Objects.requireNonNull(currentServerClient).registerEvent(event);
        }

        @Override
        public void setCurrentRoom(RoomInfo roomInfo) {
            Objects.requireNonNull(currentServerClient).setCurrentRoom(roomInfo);
        }

        @Override
        public void setGameState(ClientGameState state) {
            Objects.requireNonNull(currentServerClient).setGameState(state);
        }

        @Override
        public void setRoomList(List<RoomInfo> roomList) {
            Objects.requireNonNull(currentServerClient).setRoomList(roomList);
        }

        @Override
        public void setUserID(UserID userID) {
            if (networkStatus != NetworkStatus.ATTEMPTING)
                throw new RuntimeException("setUserId() should only be called when network status is ATTEMPTING");

            currentServerClient = new RemoteServerClient(userID, RemoteNetworkClient.this);
            networkStatus = NetworkStatus.OK;
        }

        @Override
        public void kick() {
            clearStateAndSetStatus(NetworkStatus.DISCONNECTED);
        }
    };
}
