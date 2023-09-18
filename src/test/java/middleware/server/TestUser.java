package middleware.server;

import middleware.communication.NetworkDevice;
import middleware.messages_to_client.MessageToClient;
import middleware.messages_to_server.MessageToServerFactory;
import middleware.messages_to_server.MessageToServerHandler;
import mudgame.server.state.ClassicServerStateSupplier;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public final class TestUser {
    public final User user;
    public final List<MessageToClient> sent = new ArrayList<>();
    public final NetworkDevice device = new DummyNetworkDevice();

    TestUser(GameServer server) {
        user = new User(
                new ClassicServerStateSupplier(),
                sent::add,
                device,
                server
        );
    }

    TestUser(GameServer server, String name) {
        this(server);
        receive().setName(name);
    }

    public MessageToServerHandler receive() {
        return new MessageToServerFactory(user::processMessage);
    }

    public static final class DummyNetworkDevice implements NetworkDevice {
        private final AtomicBoolean closed = new AtomicBoolean();

        @Override
        public void scheduleToClose() {
            closed.set(true);
        }

        @Override
        public void close() {
            closed.set(true);
        }

        @Override
        public boolean isClosedOrScheduledToClose() {
            return closed.get();
        }

        @Override
        public boolean isClosed() {
            return closed.get();
        }
    }
}
