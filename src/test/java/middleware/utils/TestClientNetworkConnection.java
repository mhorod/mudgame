package middleware.utils;

import middleware.clients.NetworkDevice;
import middleware.clients.NetworkDevice.NetworkDeviceBuilder;
import middleware.messages_to_client.MessageToClient;
import middleware.messages_to_client.MessageToClientFactory;
import middleware.messages_to_client.MessageToClientHandler;
import middleware.messages_to_server.MessageToServer;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

public final class TestClientNetworkConnection implements NetworkDevice, NetworkDeviceBuilder {
    public final List<MessageToServer> sent = new ArrayList<>();
    private Consumer<MessageToClient> consumer;
    private boolean isClosed = false;

    @Override
    public void close() {
        isClosed = true;
    }

    @Override
    public boolean isClosed() {
        return isClosed;
    }

    @Override
    public void send(Object obj) {
        sent.add((MessageToServer) obj);
    }

    public MessageToClientHandler receive() {
        return new MessageToClientFactory(consumer);
    }

    @Override
    public Optional<NetworkDevice> build(Consumer<Object> observer) {
        consumer = observer::accept;
        return Optional.of(this);
    }
}
