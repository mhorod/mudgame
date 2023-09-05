package middleware.remote;

import lombok.AllArgsConstructor;
import middleware.clients.Connection;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.time.Duration;

@AllArgsConstructor
public final class SocketConnection implements Connection<RemoteNetworkClient> {
    private static final Duration CONNECTION_TIMEOUT = Duration.ofSeconds(1);

    private final String address;
    private final int port;

    @Override
    public void connect(RemoteNetworkClient client) {
        new Thread(() -> connectBlocking(client)).start();
    }

    private void connectBlocking(RemoteNetworkClient client) {
        try {
            client.reportConnectionAttempt();
            Socket socket = new Socket();
            socket.connect(new InetSocketAddress(address, port), (int) CONNECTION_TIMEOUT.toMillis());
            client.setSocketConnection(socket);
        } catch (IOException ignored) {
            client.disconnect();
        }
    }
}
