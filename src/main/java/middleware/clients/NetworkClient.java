package middleware.clients;

import middleware.communication.NetworkConnectionBuilder;
import middleware.communication.NetworkDeviceBuilder;
import middleware.communication.NetworkStatus;

import java.util.Optional;

public interface NetworkClient {
    NetworkStatus getNetworkStatus();

    void disconnect();

    void connect(NetworkDeviceBuilder builder);

    void connect(NetworkConnectionBuilder builder);

    Optional<ServerClient> getServerClient();

    void processAllMessages();
}
