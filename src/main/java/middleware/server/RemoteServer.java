package middleware.server;

import lombok.extern.slf4j.Slf4j;
import middleware.communication.SocketDeviceBuilder;
import mudgame.server.state.ServerStateSupplier;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.Duration;
import java.util.Timer;
import java.util.TimerTask;

@Slf4j
public final class RemoteServer {
    private static final Duration SCAN_CLOSED_CONNECTIONS_DELAY = Duration.ofSeconds(15);

    private final GameServer server;
    private final ServerSocket serverSocket;
    private final Timer timer;

    public RemoteServer(ServerStateSupplier stateSupplier, ServerSocket serverSocket, Timer timer) {
        this.server = new GameServer(stateSupplier);
        this.serverSocket = serverSocket;
        this.timer = timer;
        new Thread(this::workReceiveConnections).start();

        timer.schedule(
                new TimerTask() {
                    @Override
                    public void run() {
                        server.checkRemoval();
                    }
                },
                SCAN_CLOSED_CONNECTIONS_DELAY.toMillis(),
                SCAN_CLOSED_CONNECTIONS_DELAY.toMillis()
        );
    }

    public void stop() {
        try {
            serverSocket.close();
        } catch (IOException exception) {
            log.debug(exception.toString());
        }

        server.stop();
        timer.cancel();
    }

    private void receiveConnection(Socket socket) {
        synchronized (server) {
            User user = server.createUser(new SocketDeviceBuilder(socket));
            log.info("New connection from {} got {}", socket.getInetAddress(), user.getUserID());
        }
    }

    private void workReceiveConnections() {
        log.info("Started listening on port {}", serverSocket.getLocalPort());

        try {
            while (!serverSocket.isClosed()) {
                Socket socket = serverSocket.accept();
                receiveConnection(socket);
            }
        } catch (IOException exception) {
            log.debug(exception.toString());
        } finally {
            stop();
        }
    }
}
