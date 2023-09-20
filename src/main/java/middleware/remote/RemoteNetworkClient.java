package middleware.remote;

import lombok.extern.slf4j.Slf4j;
import middleware.clients.NetworkClient;
import middleware.clients.NetworkDevice;
import middleware.clients.NetworkStatus;
import middleware.clients.ServerClient;
import middleware.messages_to_client.MessageToClient;
import middleware.messages_to_client.MessageToClientHandler;
import middleware.messages_to_server.MessageToServerFactory;
import middleware.messages_to_server.MessageToServerHandler;
import middleware.model.RoomInfo;
import mudgame.client.ClientGameState;
import mudgame.controls.events.Event;
import mudgame.server.state.ServerState;

import java.time.Duration;
import java.time.Instant;
import java.time.InstantSource;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

import static middleware.clients.NetworkDevice.NetworkConnectionBuilder;
import static middleware.clients.NetworkDevice.NetworkDeviceBuilder;

@Slf4j
public final class RemoteNetworkClient implements NetworkClient {
    public static final Duration CONNECTION_TIMEOUT = Duration.ofSeconds(10);
    public static final Duration PING_TIMEOUT = Duration.ofSeconds(5);
    public static final Duration PING_AFTER_IDLE = Duration.ofSeconds(15);

    // TODO remove GLOBAL_CLIENT
    public static NetworkClient GLOBAL_CLIENT = new RemoteNetworkClient(InstantSource.system());

    private final InstantSource clock;
    private final Queue<MessageToClient> messageQueue = new LinkedBlockingQueue<>();
    private final Queue<Optional<NetworkDeviceBuilder>> builderQueue = new LinkedBlockingQueue<>();

    private NetworkStatus networkStatus = NetworkStatus.DISCONNECTED;
    private NetworkDevice networkDevice;
    private RemoteServerClient currentServerClient;
    private final MessageToClientHandler messageToClientHandler = new MessageToClientHandler() {
        @Override
        public void error(String errorText) {
            // TODO somehow display it?
            log.warn(errorText);
        }

        @Override
        public void pingToClient() {
            getServerHandler().pongToServer();
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
        public void kick() {
            clearStateAndSetStatus(NetworkStatus.DISCONNECTED);
        }

        @Override
        public void changeName(String name) {
            Objects.requireNonNull(currentServerClient).changeName(name);
        }

        @Override
        public void setDownloadedState(ServerState state) {
            Objects.requireNonNull(currentServerClient).setDownloadedState(state);
        }
    };
    private Instant lastIncoming = Instant.EPOCH;
    private Instant lastPing = Instant.EPOCH;

    public RemoteNetworkClient(InstantSource clock) {
        this.clock = clock;
    }

    private void verifyNetworkStatus() {
        if (networkStatus == NetworkStatus.OK) {
            if (networkDevice.isClosed()) {
                clearStateAndSetStatus(NetworkStatus.FAILED);
                return;
            }
            if (Duration.between(lastIncoming, clock.instant()).compareTo(PING_AFTER_IDLE) < 0)
                return;
            if (lastPing.compareTo(lastIncoming) <= 0) {
                getServerHandler().pingToServer();
                lastPing = clock.instant();
            } else if (Duration.between(lastPing, clock.instant()).compareTo(PING_TIMEOUT) > 0)
                clearStateAndSetStatus(NetworkStatus.FAILED);
        }
    }

    private void clearStateAndSetStatus(NetworkStatus status) {
        if (networkDevice != null) {
            networkDevice.close();
            networkDevice = null;
            currentServerClient = null;
        }
        networkStatus = status;
    }

    public void disconnect() {
        if (networkDevice != null) {
            getServerHandler().disconnect();
            networkDevice.close();
        }
        clearStateAndSetStatus(NetworkStatus.DISCONNECTED);
        log.info("disconnect() called");
    }

    @Override
    public void connect(NetworkDeviceBuilder builder) {
        Optional<NetworkDevice> device = builder.build(messageQueue::add, MessageToClient.class);

        if (device.isEmpty())
            clearStateAndSetStatus(NetworkStatus.FAILED);
        else {
            clearStateAndSetStatus(NetworkStatus.OK);
            networkDevice = device.get();
            currentServerClient = new RemoteServerClient(this);
            lastIncoming = lastPing = clock.instant();
        }
    }

    @Override
    public void connect(NetworkConnectionBuilder builder) {
        clearStateAndSetStatus(NetworkStatus.ATTEMPTING);
        new Thread(() -> builderQueue.add(builder.connect(CONNECTION_TIMEOUT))).start();
    }

    @Override
    public NetworkStatus getNetworkStatus() {
        verifyNetworkStatus();
        return networkStatus;
    }

    @Override
    public Optional<ServerClient> getServerClient() {
        return Optional.ofNullable(currentServerClient);
    }

    @Override
    public void processAllMessages() {
        while (!builderQueue.isEmpty()) {
            Optional<? extends NetworkDeviceBuilder> builder = builderQueue.remove();
            if (builder.isPresent())
                connect(builder.get());
            else
                clearStateAndSetStatus(NetworkStatus.FAILED);
        }

        while (!messageQueue.isEmpty()) {
            MessageToClient message = messageQueue.remove();
            log.info("[REC] {}", message);
            lastIncoming = clock.instant();

            message.execute(messageToClientHandler);
        }
        verifyNetworkStatus();
    }

    public MessageToServerHandler getServerHandler() {
        return new MessageToServerFactory(message -> {
            if (networkStatus != NetworkStatus.OK) {
                log.warn("Attempting to send message using disconnected NetworkClient");
                return;
            }
            log.info("[SND]: {}", message);
            networkDevice.send(message);
        });
    }
}
