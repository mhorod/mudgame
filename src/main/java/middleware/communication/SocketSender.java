package middleware.communication;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.NotSerializableException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.Socket;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

@Slf4j
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

    @Override
    public void close() {
        try {
            socket.close();
        } catch (IOException ignored) {
        }
    }

    @Override
    public boolean isClosed() {
        return socket.isClosed();
    }

    private void work() {
        try {
            final ObjectOutputStream stream = new ObjectOutputStream(socket.getOutputStream());

            while (!socket.isClosed()) {
                T message = queue.take();
                log.debug(message.toString());
                stream.writeObject(message);
            }
        } catch (IOException | InterruptedException exception) {
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
