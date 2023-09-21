package middleware.server;

import core.model.PlayerID;
import middleware.utils.TestGameServer;
import middleware.utils.TestUser;
import org.junit.jupiter.api.Test;

import static middleware.messages_to_client.MessageToClient.PingToClientMessage;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class GameServerTest {
    @Test
    void inactive_users_are_pinged() {
        // given
        GameServer server = TestGameServer.create();

        TestUser user = new TestUser(server);
        user.sent.clear();

        // when
        server.checkRemoval();

        // then
        assertThat(user.device.isClosed()).isFalse();
        assertThat(user.sent).hasSize(1).first().isInstanceOf(PingToClientMessage.class);
        assertThat(server.getUserList()).containsExactly(user.user);
    }

    @Test
    void inactive_users_are_deleted_after_ping() {
        // given
        GameServer server = TestGameServer.create();

        TestUser user = new TestUser(server);
        user.sent.clear();

        // when
        server.checkRemoval();
        user.allowKick();
        server.checkRemoval();

        // then
        assertThat(user.device.isClosed()).isTrue();
        assertThat(server.getUserList()).isEmpty();
    }

    @Test
    void active_users_are_not_pinged() {
        // given
        GameServer server = TestGameServer.create();

        TestUser user = new TestUser(server);
        user.sent.clear();

        // when
        user.receive().pongToServer();
        server.checkRemoval();

        // then
        assertThat(user.device.isClosed()).isFalse();
        assertThat(user.sent).isEmpty();
        assertThat(server.getUserList()).containsExactly(user.user);
    }

    @Test
    void users_who_respond_to_ping_are_not_deleted() {
        // given
        GameServer server = TestGameServer.create();

        TestUser user = new TestUser(server);
        user.sent.clear();

        // when
        server.checkRemoval();
        user.receive().pongToServer();
        server.checkRemoval();

        // then
        assertThat(user.device.isClosed()).isFalse();
        assertThat(user.sent).hasSize(1);
        assertThat(server.getUserList()).containsExactly(user.user);
    }

    @Test
    void users_who_disconnected_are_deleted() {
        // given
        GameServer server = TestGameServer.create();

        TestUser user = new TestUser(server);
        user.allowKick();
        user.device.close();
        user.sent.clear();

        // when
        server.checkRemoval();

        // then
        assertThat(server.getUserList()).isEmpty();
    }

    @Test
    void different_users_are_not_mixed_up_1() {
        // given
        GameServer server = TestGameServer.create();

        TestUser user1 = new TestUser(server);
        TestUser user2 = new TestUser(server);
        TestUser user3 = new TestUser(server);
        user1.sent.clear();
        user2.sent.clear();
        user3.sent.clear();
        user3.device.close();

        // when
        user1.receive().pongToServer();
        user3.allowKick();
        server.checkRemoval();

        // then
        assertThat(user1.device.isClosed()).isFalse();
        assertThat(user1.sent).isEmpty();
        assertThat(user2.device.isClosed()).isFalse();
        assertThat(user2.sent).isNotEmpty();
        assertThat(server.getUserList()).containsExactly(user1.user, user2.user);
    }

    @Test
    void different_users_are_not_mixed_up_2() {
        // given
        GameServer server = TestGameServer.create();

        TestUser user0 = new TestUser(server);
        TestUser user1 = new TestUser(server);
        TestUser user2 = new TestUser(server);
        TestUser user3 = new TestUser(server);
        TestUser user4 = new TestUser(server);
        TestUser user5 = new TestUser(server);

        // when
        user0.allowKick();
        user0.device.close();
        user3.receive().pongToServer();
        user4.receive().pongToServer();
        server.checkRemoval();

        user1.allowKick();
        user5.allowKick();
        user5.device.close();
        user2.receive().pongToServer();
        user4.receive().pongToServer();
        server.checkRemoval();

        // then
        assertThat(user0.device.isClosed()).isTrue();
        assertThat(user1.device.isClosed()).isTrue();
        assertThat(user2.device.isClosed()).isFalse();
        assertThat(user3.device.isClosed()).isFalse();
        assertThat(user4.device.isClosed()).isFalse();
        assertThat(user5.device.isClosed()).isTrue();

        assertThat(server.getUserList()).containsExactly(user2.user, user3.user, user4.user);
    }

    @Test
    void remove_user_should_not_be_called_directly() {
        // given
        GameServer server = TestGameServer.create();
        TestUser user = new TestUser(server);
        user.allowKick();
        user.receive().disconnect();

        // then
        assertThatThrownBy(() -> server.removeUser(user.user)).isInstanceOf(
                IllegalArgumentException.class);
    }

    @Test
    void remove_room_should_not_be_called_directly() {
        // given
        GameServer server = TestGameServer.create();
        TestUser user = new TestUser(server);
        user.receive().createRoom(new PlayerID(0), 2);
        Room room = user.user.getRoom().orElseThrow();
        user.allowKick();
        user.receive().disconnect();

        // then
        assertThatThrownBy(() -> server.removeRoom(room)).isInstanceOf(
                IllegalArgumentException.class);
    }

    @Test
    void stop_disconnects_all_users() {
        // given
        GameServer server = TestGameServer.create();
        TestUser user = new TestUser(server);

        // when
        user.allowKick();
        server.stop();

        // then
        assertThat(user.device.isClosed()).isTrue();
    }

    @Test
    void stop_removes_rooms() {
        // given
        GameServer server = TestGameServer.create();
        TestUser user = new TestUser(server);
        user.receive().createRoom(new PlayerID(0), 2);

        // when
        user.allowKick();
        server.stop();

        // then
        assertThat(user.user.getRoom()).isEmpty();
        assertThat(server.getRoomInfoList()).isEmpty();
    }


}
