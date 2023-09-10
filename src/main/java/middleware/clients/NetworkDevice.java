package middleware.clients;

import java.io.Serializable;
import java.time.Duration;
import java.util.Optional;
import java.util.function.Consumer;

public interface NetworkDevice<S extends Serializable, R extends Serializable> {
    void close();

    boolean isClosed();

    void sendMessage(S message);

    interface NetworkDeviceBuilder {
        <S extends Serializable, R extends Serializable> NetworkDevice<S, R> build(Consumer<R> observer, Class<R> clazz);
    }

    interface NetworkConnectionBuilder {
        Optional<? extends NetworkDeviceBuilder> connect(Duration timeout);
    }
}
