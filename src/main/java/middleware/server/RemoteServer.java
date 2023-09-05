package middleware.server;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import middleware.communication.SocketReceiver;
import middleware.communication.SocketSender;
import middleware.messages_to_client.MessageToClient;
import middleware.messages_to_server.MessageToServer;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.Duration;
import java.util.Timer;
import java.util.TimerTask;

@Slf4j
public final class RemoteServer {
    private static final Duration SCAN_CLOSED_CONNECTIONS_DELAY = Duration.ofSeconds(15);

    private final GameServer server = new GameServer();
    private final ServerSocket serverSocket;

    public RemoteServer(ServerSocket serverSocket) {
        this.serverSocket = serverSocket;
        new Thread(this::workReceiveConnections).start();
        new Timer().schedule(
                new WorkRemoveConnections(),
                SCAN_CLOSED_CONNECTIONS_DELAY.toMillis(),
                SCAN_CLOSED_CONNECTIONS_DELAY.toMillis()
        );
    }

    @SneakyThrows(IOException.class)
    public static void main(String[] args) {
        int port = args.length > 0 ? Integer.parseInt(args[0]) : 6789;
        new RemoteServer(new ServerSocket(port));
    }

    @SneakyThrows(IOException.class)
    private void workReceiveConnections() {
        log.info("Started listening on port " + serverSocket.getLocalPort());

        while (!serverSocket.isClosed()) {
            Socket socket = serverSocket.accept();

            synchronized (server) {
                SocketSender<MessageToClient> sender = new SocketSender<>(socket);

                User user = new User(
                        sender::sendMessage,
                        sender.getClosingDevice(),
                        server
                );

                new SocketReceiver<>(
                        user::processMessage,
                        socket,
                        MessageToServer.class
                );

                log.info("New connection from %s got %s".formatted(
                        socket.getInetAddress().toString(),
                        user.getUserID().toString()
                ));
            }
        }
    }

    private class WorkRemoveConnections extends TimerTask {
        @Override
        public void run() {
            server.checkRemoval();
        }
    }
}
