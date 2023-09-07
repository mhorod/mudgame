package middleware.server;

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
    private final Timer timer;

    public RemoteServer(ServerSocket serverSocket, Timer timer) {
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

    private void workReceiveConnections() {
        log.info("Started listening on port " + serverSocket.getLocalPort());

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
