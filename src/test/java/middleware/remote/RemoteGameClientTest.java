package middleware.remote;

import core.event.Event;
import core.model.PlayerID;
import middleware.clients.GameClient;
import middleware.clients.NetworkClient;
import middleware.clients.ServerClient;
import middleware.utils.TestServerClient;
import mudgame.server.MudServerCore;
import org.junit.jupiter.api.Test;

import static middleware.messages_to_server.MessageToServer.MakeActionMessage;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

class RemoteGameClientTest {
    @Test
    void game_client_is_created_when_set_game_state_message_is_received() {
        // given
        TestServerClient testClient = new TestServerClient();
        NetworkClient networkClient = testClient.networkClient;
        ServerClient serverClient = testClient.serverClient;

        // when
        testClient.receive()
                .setGameState(MudServerCore.newState(2).toClientGameState(new PlayerID(0)));
        networkClient.processAllMessages();

        // then
        assertThat(serverClient.getGameClient().isPresent()).isTrue();
    }

    @Test
    void event_is_sent_if_client_is_active() {
        // given
        TestServerClient testClient = new TestServerClient();
        NetworkClient networkClient = testClient.networkClient;
        ServerClient serverClient = testClient.serverClient;

        testClient.receive()
                .setGameState(MudServerCore.newState(2).toClientGameState(new PlayerID(0)));
        networkClient.processAllMessages();

        GameClient gameClient = serverClient.getGameClient().orElseThrow();

        // when
        gameClient.getControls().completeTurn();

        // then
        assertThat(testClient.sent).isNotEmpty().allMatch(MakeActionMessage.class::isInstance);
    }

    @Test
    void event_is_not_sent_if_client_is_inactive() {
        // given
        TestServerClient testClient = new TestServerClient();
        NetworkClient networkClient = testClient.networkClient;
        ServerClient serverClient = testClient.serverClient;

        testClient.receive()
                .setGameState(MudServerCore.newState(2).toClientGameState(new PlayerID(0)));
        networkClient.processAllMessages();

        GameClient gameClient = serverClient.getGameClient().orElseThrow();
        testClient.receive()
                .setGameState(MudServerCore.newState(2).toClientGameState(new PlayerID(0)));
        networkClient.processAllMessages();

        // when
        gameClient.getControls().completeTurn();

        // then
        assertThat(testClient.sent).isEmpty();
    }

    @Test
    void active_client_receives_events() {
        // given
        TestServerClient testClient = new TestServerClient();
        NetworkClient networkClient = testClient.networkClient;
        ServerClient serverClient = testClient.serverClient;

        testClient.receive()
                .setGameState(MudServerCore.newState(2).toClientGameState(new PlayerID(0)));
        networkClient.processAllMessages();

        GameClient gameClient = serverClient.getGameClient().orElseThrow();

        // when
        testClient.receive().registerEvent(mock(Event.class));
        networkClient.processAllMessages();

        // then
        assertThat(gameClient.hasEvent()).isTrue();
    }

    @Test
    void inactive_client_does_not_receive_events() {
        // given
        TestServerClient testClient = new TestServerClient();
        NetworkClient networkClient = testClient.networkClient;
        ServerClient serverClient = testClient.serverClient;

        testClient.receive()
                .setGameState(MudServerCore.newState(2).toClientGameState(new PlayerID(0)));
        networkClient.processAllMessages();

        GameClient gameClient = serverClient.getGameClient().orElseThrow();
        testClient.receive()
                .setGameState(MudServerCore.newState(2).toClientGameState(new PlayerID(0)));
        networkClient.processAllMessages();

        // when
        testClient.receive().registerEvent(mock(Event.class));
        networkClient.processAllMessages();

        // then
        assertThat(gameClient.hasEvent()).isFalse();
    }
}
