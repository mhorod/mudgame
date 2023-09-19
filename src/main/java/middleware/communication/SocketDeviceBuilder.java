package middleware.communication;

import lombok.AllArgsConstructor;

import java.net.Socket;
import java.util.Optional;
import java.util.function.Consumer;

@AllArgsConstructor
public final class SocketDeviceBuilder implements NetworkDeviceBuilder {
    private final Socket socket;

    @Override
    public Optional<NetworkDevice> build(Consumer<Object> observer) {
        if (socket.isClosed())
            return Optional.empty();
        else
            return Optional.of(new SocketDevice(socket, observer));
    }
}
