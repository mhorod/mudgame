package middleware.clients;

import middleware.remote.NetworkStatus;

import java.util.Optional;

public interface NetworkClient<SELF extends NetworkClient<SELF>> {
    NetworkStatus getNetworkStatus();

    void disconnect();

    void connect(Connection<SELF> connection);

    Optional<ServerClient> getServerClient();

    void processAllMessages();
}
