package middleware.network;

import lombok.SneakyThrows;
import middleware.communication.MessageProcessor;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.net.Socket;

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

    @SneakyThrows({IOException.class, ClassNotFoundException.class})
    private void work() {
        final ObjectInputStream stream = new ObjectInputStream(socket.getInputStream());

        while (!socket.isClosed()) {
            T message = clazz.cast(stream.readObject());
            processor.processMessage(message);
        }
    }
}
