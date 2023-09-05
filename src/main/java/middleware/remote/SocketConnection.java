package middleware.remote;

import lombok.AllArgsConstructor;
import middleware.clients.Connection;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

import static middleware.remote.RemoteNetworkClient.SOCKET_CONNECTION_TIMEOUT;

@AllArgsConstructor
public final class SocketConnection implements Connection<RemoteNetworkClient> {
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
            socket.connect(new InetSocketAddress(address, port), (int) SOCKET_CONNECTION_TIMEOUT.toMillis());
            client.setSocketConnection(socket);
        } catch (IOException ignored) {
            client.disconnect();
        }
    }
}
