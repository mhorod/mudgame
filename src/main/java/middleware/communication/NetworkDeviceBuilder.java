package middleware.communication;

import java.util.Optional;
import java.util.function.Consumer;

public interface NetworkDeviceBuilder {
    Optional<NetworkDevice> build(Consumer<Object> observer);

    default <T> Optional<NetworkDevice> build(Consumer<T> observer, Class<T> clazz) {
        return build(obj -> observer.accept(clazz.cast(obj)));
    }
}
