package middleware.communicators;

import lombok.SneakyThrows;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.net.Socket;

public final class NetworkReceiver<T extends Serializable> {
    private final MessageProcessor<T> processor;
    private final Socket socket;
    private final Class<T> clazz;

    public NetworkReceiver(MessageProcessor<T> processor, Socket socket, Class<T> clazz) {
        this.processor = processor;
        this.socket = socket;
        this.clazz = clazz;
        new Thread(this::work).start();
    }

    @SneakyThrows({IOException.class, ClassNotFoundException.class})
    private void work() {
        final ObjectInputStream stream = new ObjectInputStream(socket.getInputStream());

        processor.processMessage(clazz.cast(stream.readObject()));
        throw new RuntimeException();
    }
}
