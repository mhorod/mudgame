package middleware.communication;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.NotSerializableException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.net.Socket;
import java.util.function.Consumer;

@Slf4j
public final class SocketReceiver<T extends Serializable> {
    private final Consumer<T> consumer;
    private final Socket socket;
    private final Class<T> clazz;

    public SocketReceiver(Consumer<T> consumer, Socket socket, Class<T> clazz) {
        this.consumer = consumer;
        this.socket = socket;
        this.clazz = clazz;
        new Thread(this::work).start();
    }

    private void work() {
        try {
            final ObjectInputStream stream = new ObjectInputStream(socket.getInputStream());

            while (!socket.isClosed()) {
                T message = clazz.cast(stream.readObject());
                log.debug(message.toString());
                consumer.accept(message);
            }
        } catch (IOException | ClassNotFoundException exception) {
            if (exception instanceof NotSerializableException || exception.getCause() instanceof NotSerializableException)
                log.warn(exception.toString());
            else
                log.debug(exception.toString());
            try {
                socket.close();
            } catch (IOException innerException) {
                log.debug(innerException.toString());
            }
        }
    }
}
