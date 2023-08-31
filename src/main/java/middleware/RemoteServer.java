package middleware;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import middleware.communicators.CommunicatorComposer;
import middleware.communicators.ServerSideCommunicator;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

@Slf4j
public final class RemoteServer {
    private final GameServer server = new GameServer();
    private final ServerSocket serverSocket;
    private long nextID = 0;

    public RemoteServer(ServerSocket serverSocket) {
        this.serverSocket = serverSocket;
        new Thread(this::work).start();
    }

    @SneakyThrows(IOException.class)
    public static void main(String[] args) {
        new RemoteServer(new ServerSocket(6789));
    }

    @SneakyThrows(IOException.class)
    private void work() {
        log.info("Started listening on port " + serverSocket.getLocalPort());

        while (!serverSocket.isClosed()) {
            Socket socket = serverSocket.accept();
            UserID userID = new UserID(nextID++);

            log.info("New connection from %s got %s".formatted(
                    socket.getInetAddress().toString(),
                    userID.toString()
            ));

            synchronized (this) {
                ServerSideCommunicator communicator = CommunicatorComposer.remote(server, userID, socket);
                server.addConnection(userID, communicator);
            }
        }
    }
}
