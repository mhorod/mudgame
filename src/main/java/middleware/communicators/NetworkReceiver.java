package middleware.communicators;

import lombok.SneakyThrows;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.net.Socket;

public class NetworkReceiver<T extends Serializable> implements Receiver<T> {
    final private Socket socket;
    final private MessageQueue<T> queue;
    final private Class<T> clazz;

    public NetworkReceiver(Socket socket, MessageQueue<T> queue, Class<T> clazz) {
        this.socket = socket;
        this.queue = queue;
        this.clazz = clazz;
        new Thread(this::work).start();
    }

    @Override
    public boolean hasMessage() {
        return queue.hasMessage();
    }

    @Override
    public T removeMessage() {
        return queue.removeMessage();
    }

    @Override
    public T takeMessage() throws InterruptedException {
        return queue.takeMessage();
    }

    @SneakyThrows({IOException.class, ClassNotFoundException.class})
    private void work() {
        final ObjectInputStream stream = new ObjectInputStream(socket.getInputStream());

        clazz.cast(stream.readObject());
        throw new RuntimeException();
    }
}
