package middleware.communication;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.NotSerializableException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.net.Socket;

@Slf4j
public final class SocketReceiver<T extends Serializable> {
    private final Socket socket;
    private final Class<T> clazz;
    private final MessageProcessor<T> processor;

    public SocketReceiver(Socket socket, Class<T> clazz, MessageProcessor<T> processor) {
        this.socket = socket;
        this.clazz = clazz;
        this.processor = processor;
        new Thread(this::work).start();
    }

    private void work() {
        try {
            final ObjectInputStream stream = new ObjectInputStream(socket.getInputStream());

            while (!socket.isClosed()) {
                T message = clazz.cast(stream.readObject());
                log.debug(message.toString());
                processor.processMessage(message);
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
