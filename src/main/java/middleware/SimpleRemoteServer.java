package middleware;

import lombok.SneakyThrows;
import middleware.communicators.CommunicatorComposer;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

// TODO implement network layer
public class SimpleRemoteServer extends SimpleServer {
    final ServerSocket serverSocket;

    @SneakyThrows(IOException.class)
    public SimpleRemoteServer(int port) {
        serverSocket = new ServerSocket(port);
        new Thread(this::work).start();

        throw new UnsupportedOperationException();
    }

    @SneakyThrows(IOException.class)
    private void work() {
        while (!serverSocket.isClosed()) {
            Socket socket = serverSocket.accept();
            CommunicatorComposer.remote(this, new UserID(-1), socket);
        }
    }
}
