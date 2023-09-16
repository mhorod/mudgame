package middleware.clients;

import java.time.Duration;
import java.util.Optional;
import java.util.function.Consumer;

public interface NetworkDevice {
    void close();

    boolean isClosed();

    void send(Object obj);

    interface NetworkDeviceBuilder {
        Optional<NetworkDevice> build(Consumer<Object> observer);

        default <T> Optional<NetworkDevice> build(Consumer<T> observer, Class<T> clazz) {
            return build(obj -> observer.accept(clazz.cast(obj)));
        }
    }

    interface NetworkConnectionBuilder {
        Optional<NetworkDeviceBuilder> connect(Duration timeout);
    }
}
