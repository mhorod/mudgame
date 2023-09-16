package middleware.communication;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import middleware.clients.NetworkDevice;
import org.apache.commons.lang3.exception.ExceptionUtils;

import java.io.IOException;
import java.io.NotSerializableException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
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
public final class SocketDevice implements NetworkDevice {
    private final Socket socket;
    private final Consumer<Object> observer;

    private final BlockingQueue<Optional<Object>> queue = new LinkedBlockingQueue<>();
    private final AtomicBoolean closed = new AtomicBoolean(false);

    public SocketDevice(Socket socket, Consumer<Object> observer) {
        this.socket = socket;
        this.observer = observer;

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
    public void send(Object obj) {
        if (!isClosed())
            queue.add(Optional.of(obj));
    }

    private void senderWork() {
        try {
            final ObjectOutputStream stream = new ObjectOutputStream(socket.getOutputStream());

            while (!socket.isClosed()) {
                Optional<Object> messageOrNull = queue.take();
                if (messageOrNull.isEmpty())
                    break;
                Object message = messageOrNull.get();
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
                Object message = Objects.requireNonNull(stream.readObject());
                log.debug(message.toString());
                observer.accept(message);
            }
        } catch (IOException | ClassNotFoundException exception) {
            log.debug(exception.toString());
            if (causedByNotSerializableException(exception))
                throw new RuntimeException(exception);
        } finally {
            close();
        }
    }

    private boolean causedByNotSerializableException(Exception exception) {
        return ExceptionUtils.getThrowableList(exception)
                .stream()
                .anyMatch(NotSerializableException.class::isInstance);
    }

    @AllArgsConstructor
    public static final class SocketDeviceBuilder implements NetworkDeviceBuilder {
        private final Socket socket;

        @Override
        public Optional<NetworkDevice> build(Consumer<Object> observer) {
            if (socket.isClosed())
                return Optional.empty();
            else
                return Optional.of(new SocketDevice(socket, observer));
        }
    }

    @Slf4j
    @AllArgsConstructor
    public static final class SocketConnectionBuilder implements NetworkConnectionBuilder {
        private final String address;
        private final int port;

        @Override
        public Optional<NetworkDeviceBuilder> connect(Duration timeout) {
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
