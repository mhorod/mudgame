package middleware.utils;

import middleware.remote_clients.RemoteNetworkClient;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.Month;
import java.time.ZoneOffset;

public final class TestClient {
    private Instant now = LocalDate.of(2000, Month.JANUARY, 1)
            .atStartOfDay()
            .toInstant(ZoneOffset.UTC);

    public final RemoteNetworkClient client = new RemoteNetworkClient(() -> now);

    public void advance(Duration duration) {
        now = now.plus(duration);
    }
}
