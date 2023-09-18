import lombok.SneakyThrows;
import middleware.server.RemoteServer;
import mudgame.server.state.ClassicServerStateSupplier;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.Timer;

public final class Server {
    @SneakyThrows(IOException.class)
    public static void main(String[] args) {
        int port = args.length > 0 ? Integer.parseInt(args[0]) : 6789;
        ClassicServerStateSupplier serverStateSupplier = new ClassicServerStateSupplier();
        new RemoteServer(serverStateSupplier, new ServerSocket(port), new Timer());
    }
}
