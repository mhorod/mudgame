package middleware.utils;

import middleware.clients.NetworkClient;
import middleware.clients.ServerClient;
import middleware.messages_to_client.MessageToClientHandler;
import middleware.remote_clients.RemoteNetworkClient;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.Month;
import java.time.ZoneOffset;
import java.util.List;

public final class TestServerClient {
    public final TestConnection connection = new TestConnection();
    public final ServerClient serverClient;
    public final List<Object> sent = connection.sent;
    private Instant now = LocalDate.of(2000, Month.JANUARY, 1)
            .atStartOfDay()
            .toInstant(ZoneOffset.UTC);
    public final NetworkClient networkClient = new RemoteNetworkClient(() -> now);

    public TestServerClient() {
        networkClient.connect(connection);
        serverClient = networkClient.getServerClient().orElseThrow();
    }

    public MessageToClientHandler receive() {
        return connection.receiveFromServer();
    }

    public void advance(Duration duration) {
        now = now.plus(duration);
    }
}
