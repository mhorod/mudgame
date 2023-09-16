package middleware.remote;

import lombok.SneakyThrows;
import middleware.clients.NetworkClient;
import middleware.clients.NetworkDevice.NetworkConnectionBuilder;
import middleware.clients.NetworkDevice.NetworkDeviceBuilder;
import middleware.clients.NetworkStatus;
import middleware.utils.TestClient;
import middleware.utils.TestClientNetworkConnection;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.mockito.ArgumentCaptor;

import java.time.Duration;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import static middleware.messages_to_server.MessageToServer.DisconnectMessage;
import static middleware.remote.RemoteNetworkClient.CONNECTION_TIMEOUT;
import static middleware.utils.Wait.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class RemoteNetworkClientTest {
    @Test
    void status_after_successful_connection_is_ok() {
        // given
        TestClient testClient = new TestClient();
        NetworkClient client = testClient.client;

        // when
        TestClientNetworkConnection connection = new TestClientNetworkConnection();
        client.connect(connection);

        // then
        assertThat(client.getNetworkStatus()).isEqualTo(NetworkStatus.OK);
    }

    @Test
    void status_after_spurious_disconnection_is_failed() {
        // given
        TestClient testClient = new TestClient();
        NetworkClient client = testClient.client;

        TestClientNetworkConnection connection = new TestClientNetworkConnection();
        client.connect(connection);

        // when
        connection.close();
        client.processAllMessages();

        // then
        assertThat(client.getNetworkStatus()).isEqualTo(NetworkStatus.FAILED);
    }

    @Test
    void status_after_unsuccessful_connection_is_failed() {
        // given
        TestClient testClient = new TestClient();
        NetworkClient client = testClient.client;

        // when
        NetworkDeviceBuilder builder = mock(NetworkDeviceBuilder.class);
        doReturn(Optional.empty()).when(builder).build(any());
        client.connect(builder);

        // then
        assertThat(client.getNetworkStatus()).isEqualTo(NetworkStatus.FAILED);
    }

    @Test
    void sending_message_using_disconnected_client_does_nothing_1() {
        // given
        TestClient testClient = new TestClient();
        RemoteNetworkClient client = testClient.client;

        TestClientNetworkConnection connection = new TestClientNetworkConnection();
        client.connect(connection);

        connection.close();
        client.processAllMessages();

        // when
        client.getServerHandler().pingToServer();
        client.processAllMessages();

        // then
        assertThat(connection.sent).isEmpty();
    }

    @Test
    void sending_message_using_disconnected_client_does_nothing_2() {
        // given
        TestClient testClient = new TestClient();
        NetworkClient client = testClient.client;

        TestClientNetworkConnection connection = new TestClientNetworkConnection();
        client.connect(connection);

        connection.close();
        client.processAllMessages();

        // when
        connection.receive().pingToClient();
        client.processAllMessages();

        // then
        assertThat(connection.sent).isEmpty();
    }

    @Test
    @Timeout(value = 500, unit = TimeUnit.MILLISECONDS)
    void connect_network_connection_builder_does_not_block() {
        // given
        TestClient testClient = new TestClient();
        NetworkClient client = testClient.client;

        // when
        client.connect(new NetworkConnectionBuilder() {
            @SneakyThrows
            @Override
            public Optional<NetworkDeviceBuilder> connect(Duration timeout) {
                Thread.sleep(1000);
                return Optional.empty();
            }
        });
        client.processAllMessages();

        // then
        assertThat(client.getNetworkStatus()).isEqualTo(NetworkStatus.ATTEMPTING);
    }

    @Test
    void connect_network_connection_builder_uses_correct_timeout() {
        // given
        TestClient testClient = new TestClient();
        NetworkClient client = testClient.client;

        // when
        NetworkConnectionBuilder builder = mock(NetworkConnectionBuilder.class);

        ArgumentCaptor<Duration> captor = ArgumentCaptor.forClass(Duration.class);
        doReturn(Optional.empty()).when(builder).connect(captor.capture());

        client.connect(builder);
        verify_wait();
        client.processAllMessages();

        // then
        assertThat(client.getNetworkStatus()).isEqualTo(NetworkStatus.FAILED);
        assertThat(captor.getAllValues()).hasSize(1).first().isEqualTo(CONNECTION_TIMEOUT);
    }

    @Test
    void connect_network_connection_builder_works() {
        // given
        TestClient testClient = new TestClient();
        NetworkClient client = testClient.client;

        // when
        NetworkConnectionBuilder builder = mock(NetworkConnectionBuilder.class);
        doReturn(Optional.of(new TestClientNetworkConnection())).when(builder).connect(any());

        client.connect(builder);
        verify_wait();
        client.processAllMessages();

        // then
        assertThat(client.getNetworkStatus()).isEqualTo(NetworkStatus.OK);
    }

    @Test
    void disconnecting_works() {
        // given
        TestClient testClient = new TestClient();
        NetworkClient client = testClient.client;

        TestClientNetworkConnection connection = new TestClientNetworkConnection();
        client.connect(connection);
        client.processAllMessages();

        // when
        client.disconnect();
        client.processAllMessages();

        // then
        assertThat(connection.sent).hasSize(1).first().isInstanceOf(DisconnectMessage.class);
        assertThat(client.getNetworkStatus()).isEqualTo(NetworkStatus.DISCONNECTED);
        assertThat(connection.isClosed()).isTrue();
    }

    @Test
    void error_does_not_close_the_connection() {
        // given
        TestClient testClient = new TestClient();
        NetworkClient client = testClient.client;

        TestClientNetworkConnection connection = new TestClientNetworkConnection();
        client.connect(connection);

        // when
        connection.receive().error("");
        client.processAllMessages();

        // then
        assertThat(connection.sent).isEmpty();
        assertThat(client.getNetworkStatus()).isEqualTo(NetworkStatus.OK);
        assertThat(connection.isClosed()).isFalse();
    }

    @Test
    void kick_closes_the_connection() {
        // given
        TestClient testClient = new TestClient();
        NetworkClient client = testClient.client;

        TestClientNetworkConnection connection = new TestClientNetworkConnection();
        client.connect(connection);

        // when
        connection.receive().kick();
        client.processAllMessages();

        // then
        assertThat(connection.sent).isEmpty();
        assertThat(client.getNetworkStatus()).isEqualTo(NetworkStatus.DISCONNECTED);
        assertThat(connection.isClosed()).isTrue();
    }

}
