package middleware.communication;

import java.time.Duration;
import java.util.Optional;

public interface NetworkConnectionBuilder {
    Optional<NetworkDeviceBuilder> connect(Duration timeout);
}
