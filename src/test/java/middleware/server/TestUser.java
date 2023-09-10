package middleware.server;

import middleware.clients.NetworkDevice;
import middleware.clients.NetworkDevice.NetworkDeviceBuilder;
import middleware.messages_to_client.MessageToClient;
import middleware.messages_to_server.MessageToServer;
import middleware.messages_to_server.MessageToServerFactory;
import middleware.messages_to_server.MessageToServerHandler;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

public final class TestUser {
    public final User user;
    public final List<MessageToClient> sent = new ArrayList<>();
    public final DummyNetworkDevice device = new DummyNetworkDevice();
    public Consumer<MessageToServer> consumer;

    TestUser(GameServer server) {
        user = new User(new DummyNetworkDeviceBuilder(), server);
    }

    TestUser(GameServer server, String name) {
        this(server);
        receive().setName(name);
    }

    public MessageToServerHandler receive() {
        return new MessageToServerFactory(consumer);
    }

    public final class DummyNetworkDevice implements NetworkDevice<MessageToClient, MessageToServer> {
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
        public void sendMessage(MessageToClient message) {
            sent.add(message);
        }
    }

    @SuppressWarnings("unchecked")
    public final class DummyNetworkDeviceBuilder implements NetworkDeviceBuilder {
        @Override
        public <S extends Serializable, R extends Serializable> NetworkDevice<S, R> build(Consumer<R> observer, Class<R> clazz) {
            consumer = (Consumer<MessageToServer>) observer;
            return (NetworkDevice<S, R>) device;
        }
    }
}
