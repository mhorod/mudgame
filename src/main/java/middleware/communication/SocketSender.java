package middleware.communication;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.NotSerializableException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.Socket;
import java.util.Optional;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

@Slf4j
public final class SocketSender<T extends Serializable> {
    private final BlockingQueue<Optional<T>> queue = new LinkedBlockingQueue<>();
    private final Socket socket;
    private volatile boolean scheduledToClose = false;

    public SocketSender(Socket socket) {
        this.socket = socket;
        new Thread(this::work).start();
    }

    public void sendMessage(T message) {
        queue.add(Optional.of(message));
    }

    private void work() {
        try {
            final ObjectOutputStream stream = new ObjectOutputStream(socket.getOutputStream());

            while (!socket.isClosed()) {
                Optional<T> messageOrNull = queue.take();
                if (messageOrNull.isEmpty())
                    break;
                T message = messageOrNull.get();
                log.debug(message.toString());
                stream.writeObject(message);
            }
        } catch (IOException | InterruptedException exception) {
            log.debug(exception.toString());
            if (exception instanceof NotSerializableException || exception.getCause() instanceof NotSerializableException)
                throw new RuntimeException(exception);
        } finally {
            try {
                socket.close();
            } catch (IOException innerException) {
                log.debug(innerException.toString());
            }
        }
    }

    public NetworkDevice getClosingDevice() {
        return new NetworkDevice() {
            @Override
            public void scheduleToClose() {
                scheduledToClose = true;
                queue.add(Optional.empty());
            }

            @Override
            public void close() {
                try {
                    socket.close();
                } catch (IOException exception) {
                    log.debug(exception.toString());
                }
            }

            @Override
            public boolean isClosed() {
                return socket.isClosed();
            }

            @Override
            public boolean isClosedOrScheduledToClose() {
                return socket.isClosed() || scheduledToClose;
            }
        };
    }
}
