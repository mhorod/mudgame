package middleware.remote_clients;

import core.model.PlayerID;
import lombok.extern.slf4j.Slf4j;
import middleware.clients.NetworkClient;
import middleware.clients.ServerClient;
import middleware.communication.NetworkConnectionBuilder;
import middleware.communication.NetworkDevice;
import middleware.communication.NetworkDeviceBuilder;
import middleware.communication.NetworkStatus;
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
import java.util.Optional;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

@Slf4j
public final class RemoteNetworkClient implements NetworkClient {
    public static final Duration CONNECTION_TIMEOUT = Duration.ofSeconds(10);
    public static final Duration PING_TIMEOUT = Duration.ofSeconds(5);
    public static final Duration PING_AFTER_IDLE = Duration.ofSeconds(15);

    // TODO remove GLOBAL_CLIENT
    public static NetworkClient GLOBAL_CLIENT = new RemoteNetworkClient(InstantSource.system());

    private final InstantSource clock;
    private final Queue<Optional<NetworkDeviceBuilder>> builderQueue = new LinkedBlockingQueue<>();

    private NetworkStatus networkStatus = NetworkStatus.DISCONNECTED;
    private Optional<Connection> currentConnection = Optional.empty();

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

        private RemoteServerClient getServerClient() {
            return currentConnection.orElseThrow().serverClient();
        }

        @Override
        public void registerEvent(Event event) {
            getServerClient().registerEvent(event);
        }

        @Override
        public void setCurrentRoom(RoomInfo roomInfo, boolean isOwner, PlayerID myPlayerID) {
            getServerClient().setCurrentRoom(roomInfo, isOwner, myPlayerID);
        }

        @Override
        public void setGameState(ClientGameState state) {
            getServerClient().setGameState(state);
        }

        @Override
        public void setRoomList(List<RoomInfo> roomList) {
            getServerClient().setRoomList(roomList);
        }

        @Override
        public void kick() {
            clearStateAndSetStatus(NetworkStatus.DISCONNECTED);
        }

        @Override
        public void changeName(String name) {
            getServerClient().changeName(name);
        }

        @Override
        public void setDownloadedState(ServerState state) {
            getServerClient().setDownloadedState(state);
        }
    };
    private Instant lastIncoming = Instant.EPOCH;
    private Instant lastPing = Instant.EPOCH;

    private record Connection(NetworkDevice networkDevice, RemoteServerClient serverClient,
                              Queue<MessageToClient> messageQueue) { }

    public RemoteNetworkClient(InstantSource clock) {
        this.clock = clock;
    }

    private void verifyNetworkStatus() {
        if (networkStatus == NetworkStatus.OK) {
            if (currentConnection.orElseThrow().networkDevice().isClosed()) {
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
        if (currentConnection.isPresent()) {
            currentConnection.orElseThrow().networkDevice().close();
            currentConnection = Optional.empty();
        }
        networkStatus = status;
    }

    public void disconnect() {
        if (currentConnection.isPresent())
            getServerHandler().disconnect();
        clearStateAndSetStatus(NetworkStatus.DISCONNECTED);
        log.info("disconnect() called");
    }

    @Override
    public void connect(NetworkDeviceBuilder builder) {
        Queue<MessageToClient> messageQueue = new LinkedBlockingQueue<>();
        Optional<NetworkDevice> device = builder.build(messageQueue::add, MessageToClient.class);

        if (device.isEmpty())
            clearStateAndSetStatus(NetworkStatus.FAILED);
        else {
            clearStateAndSetStatus(NetworkStatus.OK);
            currentConnection = Optional.of(new Connection(
                    device.orElseThrow(),
                    new RemoteServerClient(this),
                    messageQueue)
            );
            lastIncoming = lastPing = clock.instant();
        }
        log.info("connect(NetworkDeviceBuilder) called, status is {}", networkStatus);
    }

    @Override
    public void connect(NetworkConnectionBuilder builder) {
        clearStateAndSetStatus(NetworkStatus.ATTEMPTING);
        new Thread(() -> builderQueue.add(builder.connect(CONNECTION_TIMEOUT))).start();
        log.info("connect(NetworkConnectionBuilder) called, status is {}", networkStatus);
    }

    @Override
    public NetworkStatus getNetworkStatus() {
        return networkStatus;
    }

    @Override
    public Optional<ServerClient> getServerClient() {
        return currentConnection.map(Connection::serverClient).map(ServerClient.class::cast);
    }

    @Override
    public void processAllMessages() {
        log.debug("processAllMessages() called, current status is {}", networkStatus);

        while (!builderQueue.isEmpty()) {
            Optional<? extends NetworkDeviceBuilder> builder = builderQueue.remove();
            if (builder.isPresent())
                connect(builder.orElseThrow());
            else
                clearStateAndSetStatus(NetworkStatus.FAILED);
        }

        if (networkStatus != NetworkStatus.OK)
            return;

        Queue<MessageToClient> messageQueue = currentConnection.orElseThrow().messageQueue();
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
            currentConnection.orElseThrow().networkDevice().send(message);
        });
    }
}
