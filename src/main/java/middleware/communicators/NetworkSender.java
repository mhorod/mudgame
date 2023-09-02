package middleware.communicators;

import lombok.SneakyThrows;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.Socket;

public final class NetworkSender<T extends Serializable> implements Sender<T> {
    private final Socket socket;
    private final MessageQueue<T> queue;

    public NetworkSender(Socket socket, MessageQueue<T> queue) {
        this.socket = socket;
        this.queue = queue;
        new Thread(this::work).start();
    }

    @Override
    public void sendMessage(T message) {
        queue.addMessage(message);
    }

    @SneakyThrows(IOException.class)
    private void work() {
        final ObjectOutputStream stream = new ObjectOutputStream(socket.getOutputStream());

        stream.writeObject(null);
        throw new RuntimeException();
    }
}
