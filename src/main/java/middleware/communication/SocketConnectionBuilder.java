package middleware.communication;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.time.Duration;
import java.util.Optional;

@Slf4j
@AllArgsConstructor
public final class SocketConnectionBuilder implements NetworkConnectionBuilder {
    private final String address;
    private final int port;

    @Override
    public Optional<NetworkDeviceBuilder> connect(Duration timeout) {
        try {
            Socket socket = new Socket();
            socket.connect(new InetSocketAddress(address, port), (int) timeout.toMillis());
            return Optional.of(new SocketDeviceBuilder(socket));
        } catch (IOException exception) {
            log.debug(exception.toString());
            return Optional.empty();
        }
    }
}
