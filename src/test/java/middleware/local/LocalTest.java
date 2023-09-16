package middleware.local;

import core.model.PlayerID;
import middleware.clients.GameClient;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class LocalTest {
    @Test
    void local_clients_are_in_correct_order() {
        // given
        LocalServer server = new LocalServer(2);

        // when
        GameClient client0 = server.getClient(0);
        GameClient client1 = server.getClient(1);

        // then
        assertThat(server.playerCount()).isEqualTo(2);
        assertThat(server.getClients()).containsExactly(client0, client1);
        assertThat(server.state().turnManager().players()).containsExactly(client0.myPlayerID(),
                                                                           client1.myPlayerID());
    }

    @Test
    void restarting_game_works() {
        // given
        LocalServer server0 = new LocalServer(2);
        server0.getClient(0).getControls().completeTurn();
        PlayerID player0 = server0.getClient(0).myPlayerID();
        PlayerID player1 = server0.getClient(1).myPlayerID();

        // when
        LocalServer server1 = new LocalServer(server0.state());

        // then
        assertThat(server1.playerCount()).isEqualTo(2);
        assertThat(server1.state().turnManager().players()).containsExactly(player0, player1);
        assertThat(server1.state().turnManager().currentPlayer()).isEqualTo(player1);
    }

    @Test
    void events_are_registered_automatically() {
        // given
        LocalServer server = new LocalServer(2);

        GameClient client0 = server.getClient(0);
        GameClient client1 = server.getClient(1);

        // when
        client0.getControls().completeTurn();

        // then
        assertThat(client1.peekEvent().isPresent()).isTrue();
        assertThat(client1.hasEvent()).isTrue();
    }

    @Test
    void processing_events_works() {
        // given
        LocalServer server = new LocalServer(2);

        GameClient client0 = server.getClient(0);
        GameClient client1 = server.getClient(1);

        client0.getControls().completeTurn();

        // when
        while (client1.hasEvent())
            client1.processEvent();

        // then
        assertThat(client1.peekEvent().isPresent()).isFalse();
        assertThat(client1.hasEvent()).isFalse();
        assertThat(client1.getCore().turnView().isMyTurn()).isTrue();
    }
}
