package middleware;

import middleware.network.ConnectionStatus;

import java.util.Optional;

public interface Client {
    void processAllMessages();

    boolean hasCoreChanged();

    Optional<GameClient> getGameClient();

    ConnectionStatus getNetworkStatus();

    void disconnect();

    void connectAsynchronously(String host, int port);
}
