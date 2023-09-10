package middleware.communication;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import middleware.clients.NetworkDevice;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.time.Duration;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

@Slf4j
public final class SocketDevice<S extends Serializable, R extends Serializable> implements NetworkDevice<S, R> {
    private final Socket socket;
    private final Consumer<R> observer;
    private final Class<R> clazz;

    private final BlockingQueue<Optional<S>> queue = new LinkedBlockingQueue<>();
    private final AtomicBoolean closed = new AtomicBoolean(false);

    public SocketDevice(Socket socket, Consumer<R> observer, Class<R> clazz) {
        this.socket = socket;
        this.observer = observer;
        this.clazz = clazz;

        new Thread(this::senderWork).start();
        new Thread(this::receiverWork).start();
    }

    @Override
    public void close() {
        if (!closed.getAndSet(true))
            queue.add(Optional.empty());
    }

    @Override
    public boolean isClosed() {
        return closed.get() || socket.isClosed();
    }

    @Override
    public void sendMessage(S message) {
        if (!isClosed())
            queue.add(Optional.of(message));
    }

    private void senderWork() {
        try {
            final ObjectOutputStream stream = new ObjectOutputStream(socket.getOutputStream());

            while (!socket.isClosed()) {
                Optional<S> messageOrNull = queue.take();
                if (messageOrNull.isEmpty())
                    break;
                S message = messageOrNull.get();
                log.debug(message.toString());
                stream.writeObject(message);
            }
        } catch (IOException | InterruptedException exception) {
            log.debug(exception.toString());
            if (causedByNotSerializableException(exception))
                throw new RuntimeException(exception);
        } finally {
            try {
                socket.close();
            } catch (IOException innerException) {
                log.debug(innerException.toString());
            }
        }
    }

    private void receiverWork() {
        try {
            final ObjectInputStream stream = new ObjectInputStream(socket.getInputStream());

            while (!isClosed()) {
                R message = Objects.requireNonNull(clazz.cast(stream.readObject()));
                log.debug(message.toString());
                observer.accept(message);
            }
        } catch (IOException | ClassNotFoundException | NullPointerException exception) {
            log.debug(exception.toString());
            if (causedByNotSerializableException(exception)) {
                close();
                throw new RuntimeException(exception);
            }
        }
    }

    private boolean causedByNotSerializableException(Exception exception) {
        Throwable throwable = exception;
        while (throwable != null) {
            if (throwable instanceof NotSerializableException)
                return true;
            throwable = throwable.getCause();
        }
        return false;
    }

    @AllArgsConstructor
    public static final class SocketDeviceBuilder implements NetworkDeviceBuilder {
        private final Socket socket;

        @Override
        public <V extends Serializable, U extends Serializable> SocketDevice<V, U> build(Consumer<U> observer, Class<U> clazz) {
            return new SocketDevice<>(socket, observer, clazz);
        }
    }

    @Slf4j
    @AllArgsConstructor
    public static final class SocketConnectionBuilder implements NetworkConnectionBuilder {
        private final String address;
        private final int port;

        @Override
        public Optional<SocketDeviceBuilder> connect(Duration timeout) {
            try {
                Socket socket = new Socket();
                socket.connect(new InetSocketAddress(address, port), (int) timeout.toMillis());
                return Optional.of(new SocketDeviceBuilder(socket));
            } catch (IOException exception) {
                log.debug(exception.toString());
                return Optional.empty();
            }
        }
    }
}
