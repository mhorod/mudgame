package middleware;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class SimpleLocalServerTest {
    @Test
    void client_list_is_of_correct_size() {
        // when
        SimpleLocalServer server = new SimpleLocalServer(5);

        // then
        assertThat(server.clients).hasSize(5);
    }

    @Test
    void clients_ids_are_unique() {
        // given
        SimpleLocalServer server = new SimpleLocalServer(5);

        // when
        for (Client cl : server.clients)
            cl.processAllMessages();

        // then
        assertThat(server.clients.stream().map(Client::myPlayerID).toList()).doesNotHaveDuplicates();
    }

    @Test
    void server_constructor_throws_with_non_positive_player_count() {
        assertThatThrownBy(() -> new SimpleLocalServer(-5)).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> new SimpleLocalServer(-1)).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> new SimpleLocalServer(0)).isInstanceOf(IllegalArgumentException.class);
    }
}
