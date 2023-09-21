package middleware.communication;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;

import java.io.IOException;
import java.io.NotSerializableException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
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
                Object message = messageOrNull.orElseThrow();
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

}
