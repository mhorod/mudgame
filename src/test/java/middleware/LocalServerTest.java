package middleware;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class LocalServerTest {
    @Test
    void client_list_is_of_correct_size() {
        // when
        List<Client> clients = LocalServer.of(5);

        // then
        assertThat(clients).hasSize(5);
    }

    @Test
    void clients_ids_are_unique() {
        // given
        List<Client> clients = LocalServer.of(5);

        // when
        for (Client cl : clients)
            cl.processAllMessages();

        // then
        assertThat(clients.stream().map(Client::myPlayerID).toList()).doesNotHaveDuplicates();
    }

    @Test
    void server_constructor_throws_with_non_positive_player_count() {
        assertThatThrownBy(() -> LocalServer.of(-5)).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> LocalServer.of(-1)).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> LocalServer.of(0)).isInstanceOf(IllegalArgumentException.class);
    }
}
