package middleware.communication;

import lombok.SneakyThrows;
import middleware.communication.Sender;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.Socket;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public final class SocketSender<T extends Serializable> implements Sender<T> {
    private final BlockingQueue<T> queue = new LinkedBlockingQueue<>();
    private final Socket socket;

    public SocketSender(Socket socket) {
        this.socket = socket;
        new Thread(this::work).start();
    }

    @Override
    public void sendMessage(T message) {
        queue.add(message);
    }

    @SneakyThrows({IOException.class, InterruptedException.class})
    private void work() {
        final ObjectOutputStream stream = new ObjectOutputStream(socket.getOutputStream());

        while (!socket.isClosed()) {
            T message = queue.take();
            stream.writeObject(message);
        }
    }
}
