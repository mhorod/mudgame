package middleware.utils;

import middleware.clients.NetworkDevice;
import middleware.clients.NetworkDevice.NetworkDeviceBuilder;
import middleware.messages_to_client.MessageToClient;
import middleware.messages_to_client.MessageToClient.KickMessage;
import middleware.messages_to_server.MessageToServer;
import middleware.messages_to_server.MessageToServerFactory;
import middleware.messages_to_server.MessageToServerHandler;
import middleware.server.GameServer;
import middleware.server.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

public final class TestUser {
    public final User user;
    public final DummyNetworkDevice device = new DummyNetworkDevice();
    public final List<MessageToClient> sent = new ArrayList<>();
    private Consumer<MessageToServer> consumer;
    private boolean allowKick = false;

    public TestUser(GameServer server) {
        user = new User(new DummyNetworkDeviceBuilder(), server);
    }

    public TestUser(GameServer server, String name) {
        this(server);
        receive().setName(name);
    }

    public MessageToServerHandler receive() {
        return new MessageToServerFactory(consumer);
    }

    public void allowKick() {
        allowKick = true;
    }

    public final class DummyNetworkDevice implements NetworkDevice {
        private final AtomicBoolean closed = new AtomicBoolean();

        @Override
        public void close() {
            closed.set(true);
        }

        @Override
        public boolean isClosed() {
            return closed.get();
        }

        @Override
        public void send(Object obj) {
            if (!allowKick && obj instanceof KickMessage)
                throw new RuntimeException(user.getUserID() + " unexpectedly kicked!");
            sent.add((MessageToClient) obj);
        }
    }

    public final class DummyNetworkDeviceBuilder implements NetworkDeviceBuilder {
        @Override
        public Optional<NetworkDevice> build(Consumer<Object> observer) {
            consumer = observer::accept;
            return Optional.of(device);
        }
    }
}
