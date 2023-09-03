package middleware.network;

import lombok.SneakyThrows;
import middleware.communication.MessageProcessor;
import middleware.communication.Sender;

import java.io.Closeable;
import java.io.IOException;
import java.io.Serializable;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.time.Duration;
import java.time.Instant;

public abstract class AbstractNetworkController<S extends Serializable, R extends Serializable> {
    private static final Duration CONNECTION_TIMEOUT = Duration.ofSeconds(1);
    private static final Duration BEFORE_PING = Duration.ofSeconds(30);
    private static final Duration PING_TIMEOUT = Duration.ofSeconds(5);

    private final MessageProcessor<R> processor;
    private final Class<R> clazz;

    private ConnectionStatus status = ConnectionStatus.DISCONNECTED;
    private Closeable toClose;
    private Sender<S> sender;
    private Instant lastIncoming = Instant.EPOCH, lastPing = Instant.EPOCH;

    public AbstractNetworkController(MessageProcessor<R> processor, Class<R> clazz) {
        this.processor = processor;
        this.clazz = clazz;
    }

    public void sendMessage(S message) {
        if (status == ConnectionStatus.OK)
            sender.sendMessage(message);
        else
            throw new RuntimeException("Attempting to use disconnected NetworkController");
    }

    public ConnectionStatus getStatus() {
        verifyStatus();
        return status;
    }

    public void verifyStatus() {
        if (status != ConnectionStatus.OK)
            return;
        if (Duration.between(lastIncoming, Instant.now()).compareTo(BEFORE_PING) < 0)
            return;

        if (lastPing.compareTo(lastIncoming) <= 0) {
            sendPingMessage();
            lastPing = Instant.now();
        } else if (Duration.between(lastPing, Instant.now()).compareTo(PING_TIMEOUT) > 0)
            disconnect();
    }

    abstract protected void sendPingMessage();

    @SneakyThrows(IOException.class)
    public void disconnect() {
        if (status == ConnectionStatus.OK) {
            toClose.close();
            toClose = null;
            sender = null;
        }
        status = ConnectionStatus.DISCONNECTED;
    }

    public void setSocketConnection(Socket socket) {
        disconnect();
        if (!socket.isConnected() || socket.isClosed())
            return;

        toClose = socket;
        sender = new SocketSender<>(socket);
        new SocketReceiver<>(socket, clazz, this::processMessage);

        status = ConnectionStatus.OK;
    }

    public void connectSocketAsynchronously(String host, int port) {
        disconnect();
        status = ConnectionStatus.ATTEMPTING;

        new Thread(() -> {
            Socket socket = new Socket();
            try {
                socket.connect(new InetSocketAddress(host, port), (int) CONNECTION_TIMEOUT.toMillis());
                setSocketConnection(socket);
            } catch (IOException e) {
                disconnect();
                status = ConnectionStatus.FAILED;
            }
        }).start();
    }

    private void processMessage(R message) {
        lastIncoming = Instant.now();
        processor.processMessage(message);
    }
}
