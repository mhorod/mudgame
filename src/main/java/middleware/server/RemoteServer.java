package middleware.server;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import middleware.model.UserID;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.Duration;
import java.util.HashSet;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

@Slf4j
public final class RemoteServer {
    private static final Duration CLEAR_CONNECTION_DELAY = Duration.ofSeconds(15);

    private final GameServer server = new GameServer();
    private final ServerSocket serverSocket;
    private final Set<ServerConnector> connectorSet = new HashSet<>();
    private long nextID = 0;

    public RemoteServer(ServerSocket serverSocket) {
        this.serverSocket = serverSocket;
        new Thread(this::workReceiveConnections).start();
        new Timer().schedule(
                new WorkRemoveConnections(),
                CLEAR_CONNECTION_DELAY.toMillis(),
                CLEAR_CONNECTION_DELAY.toMillis()
        );
    }

    @SneakyThrows(IOException.class)
    public static void main(String[] args) {
        new RemoteServer(new ServerSocket(6789));
    }

    @SneakyThrows(IOException.class)
    private void workReceiveConnections() {
        log.info("Started listening on port " + serverSocket.getLocalPort());

        while (!serverSocket.isClosed()) {
            Socket socket = serverSocket.accept();
            UserID userID = new UserID(nextID++);

            log.info("New connection from %s got %s".formatted(
                    socket.getInetAddress().toString(),
                    userID.toString()
            ));

            synchronized (server) {
                connectorSet.add(new ServerConnector(userID, server, socket));
            }
        }
    }

    private class WorkRemoveConnections extends TimerTask {
        @Override
        public void run() {
            synchronized (server) {
                for (ServerConnector connector : connectorSet.stream().toList()) {
                    connector.tick();
                    if (connector.isClosed())
                        connectorSet.remove(connector);
                }
            }
        }
    }
}
