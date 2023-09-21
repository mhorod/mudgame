package middleware.utils;

import middleware.communication.NetworkDevice;
import middleware.communication.NetworkDeviceBuilder;
import middleware.messages_to_client.MessageToClientFactory;
import middleware.messages_to_client.MessageToClientHandler;
import middleware.messages_to_server.MessageToServerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Predicate;

public final class TestConnection implements NetworkDevice, NetworkDeviceBuilder {
    public final List<Object> sent = new ArrayList<>();

    private Consumer<Object> consumer;
    private boolean isClosed = false;
    private Optional<Predicate<Object>> sendFilter = Optional.empty();

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
        if (sendFilter.isPresent() && !sendFilter.get().test(obj))
            throw new RuntimeException("Send filter predicate is not satisfied");
        sent.add(obj);
    }

    public TestConnection setSendFilter(Predicate<Object> filter) {
        sendFilter = Optional.of(filter);
        return this;
    }

    public TestConnection clearSendFilter() {
        sendFilter = Optional.empty();
        return this;
    }

    public MessageToClientHandler receiveFromServer() {
        return new MessageToClientFactory(consumer::accept);
    }

    public MessageToServerFactory receiveFromClient() {
        return new MessageToServerFactory(consumer::accept);
    }

    @Override
    public Optional<NetworkDevice> build(Consumer<Object> observer) {
        consumer = observer;
        return Optional.of(this);
    }
}
