package middleware.clients;

import java.util.Optional;

import static middleware.clients.NetworkDevice.NetworkConnectionBuilder;
import static middleware.clients.NetworkDevice.NetworkDeviceBuilder;

public interface NetworkClient {
    NetworkStatus getNetworkStatus();

    void disconnect();

    void connect(NetworkDeviceBuilder builder);

    void connect(NetworkConnectionBuilder builder);

    Optional<ServerClient> getServerClient();

    void processAllMessages();
}
