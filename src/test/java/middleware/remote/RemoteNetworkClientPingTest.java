package middleware.remote;

import middleware.clients.NetworkClient;
import middleware.communication.NetworkStatus;
import middleware.utils.TestClient;
import middleware.utils.TestConnection;
import org.junit.jupiter.api.Test;

import static middleware.messages_to_server.MessageToServer.PingToServerMessage;
import static middleware.messages_to_server.MessageToServer.PongToServerMessage;
import static middleware.remote_clients.RemoteNetworkClient.PING_AFTER_IDLE;
import static middleware.remote_clients.RemoteNetworkClient.PING_TIMEOUT;
import static middleware.utils.Wait.EPS;
import static org.assertj.core.api.Assertions.assertThat;

public class RemoteNetworkClientPingTest {
    @Test
    void client_responds_to_ping() {
        // given
        TestClient testClient = new TestClient();
        NetworkClient client = testClient.client;

        TestConnection connection = new TestConnection();
        client.connect(connection);

        // when
        connection.receiveFromServer().pingToClient();
        client.processAllMessages();

        // then
        assertThat(connection.sent).hasSize(1).first().isInstanceOf(PongToServerMessage.class);
    }

    @Test
    void client_does_not_respond_to_pong() {
        // given
        TestClient testClient = new TestClient();
        NetworkClient client = testClient.client;

        TestConnection connection = new TestConnection();
        client.connect(connection);

        // when
        connection.receiveFromServer().pongToClient();
        client.processAllMessages();

        // then
        assertThat(connection.sent).isEmpty();
    }

    @Test
    void ping_is_not_send_immediately() {
        // given
        TestClient testClient = new TestClient();
        NetworkClient client = testClient.client;

        TestConnection connection = new TestConnection();
        client.connect(connection);

        // when
        testClient.advance(PING_AFTER_IDLE.minus(EPS));
        client.processAllMessages();

        // then
        assertThat(connection.sent).noneMatch(PingToServerMessage.class::isInstance);
    }

    @Test
    void ping_is_send_after_idle_time() {
        // given
        TestClient testClient = new TestClient();
        NetworkClient client = testClient.client;

        TestConnection connection = new TestConnection();
        client.connect(connection);

        // when
        testClient.advance(PING_AFTER_IDLE.plus(EPS));
        client.processAllMessages();

        // then
        assertThat(connection.sent).anyMatch(PingToServerMessage.class::isInstance);
    }

    @Test
    void only_one_ping_is_send_after_idle_time() {
        // given
        TestClient testClient = new TestClient();
        NetworkClient client = testClient.client;

        TestConnection connection = new TestConnection();
        client.connect(connection);

        // when
        testClient.advance(PING_AFTER_IDLE.plus(EPS));
        client.processAllMessages();
        testClient.advance(EPS);
        client.processAllMessages();
        testClient.advance(EPS);
        client.processAllMessages();

        // then
        assertThat(connection.sent).hasSize(1).first().isInstanceOf(PingToServerMessage.class);
    }

    @Test
    void connection_is_closed_if_server_does_not_respond_to_ping_in_time() {
        // given
        TestClient testClient = new TestClient();
        NetworkClient client = testClient.client;

        TestConnection connection = new TestConnection();
        client.connect(connection);

        testClient.advance(PING_AFTER_IDLE.plus(EPS));
        client.processAllMessages();

        // when
        testClient.advance(PING_TIMEOUT.plus(EPS));
        client.processAllMessages();

        // then
        assertThat(client.getNetworkStatus()).isEqualTo(NetworkStatus.FAILED);
        assertThat(connection.isClosed()).isTrue();
    }

    @Test
    void connection_is_not_closed_immediately_if_server_does_not_respond_to_ping() {
        // given
        TestClient testClient = new TestClient();
        NetworkClient client = testClient.client;

        TestConnection connection = new TestConnection();
        client.connect(connection);

        testClient.advance(PING_AFTER_IDLE.plus(EPS));
        client.processAllMessages();

        // when
        testClient.advance(PING_TIMEOUT.minus(EPS));
        client.processAllMessages();

        // then
        assertThat(client.getNetworkStatus()).isEqualTo(NetworkStatus.OK);
        assertThat(connection.isClosed()).isFalse();
    }

    @Test
    void connection_is_not_closed_if_server_responds_to_ping_1() {
        // given
        TestClient testClient = new TestClient();
        NetworkClient client = testClient.client;

        TestConnection connection = new TestConnection();
        client.connect(connection);

        testClient.advance(PING_AFTER_IDLE.plus(EPS));
        client.processAllMessages();

        // when
        testClient.advance(PING_TIMEOUT.minus(EPS));
        connection.receiveFromServer().pongToClient();
        client.processAllMessages();
        testClient.advance(EPS.multipliedBy(2));
        client.processAllMessages();

        // then
        assertThat(client.getNetworkStatus()).isEqualTo(NetworkStatus.OK);
        assertThat(connection.isClosed()).isFalse();
    }

    @Test
    void connection_is_not_closed_if_server_responds_to_ping_2() {
        // given
        TestClient testClient = new TestClient();
        NetworkClient client = testClient.client;

        TestConnection connection = new TestConnection();
        client.connect(connection);

        testClient.advance(PING_AFTER_IDLE.plus(EPS));
        client.processAllMessages();

        // when
        testClient.advance(PING_TIMEOUT.plus(EPS));
        connection.receiveFromServer().pongToClient();
        client.processAllMessages();

        // then
        assertThat(client.getNetworkStatus()).isEqualTo(NetworkStatus.OK);
        assertThat(connection.isClosed()).isFalse();
    }
}
