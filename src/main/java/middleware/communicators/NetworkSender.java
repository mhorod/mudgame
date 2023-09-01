package middleware.communicators;

import lombok.SneakyThrows;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.Socket;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public final class NetworkSender<T extends Serializable> implements Sender<T> {
    private final BlockingQueue<T> queue = new LinkedBlockingQueue<>();
    private final Socket socket;

    public NetworkSender(Socket socket) {
        this.socket = socket;
        new Thread(this::work).start();
    }

    @Override
    public void sendMessage(T message) {
        queue.add(message);
    }

    @SneakyThrows(IOException.class)
    private void work() {
        final ObjectOutputStream stream = new ObjectOutputStream(socket.getOutputStream());

        stream.writeObject(queue.remove());
        throw new RuntimeException();
    }
}
