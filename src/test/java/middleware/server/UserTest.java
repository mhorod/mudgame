package middleware.server;

import core.model.PlayerID;
import middleware.model.UserID;
import middleware.utils.TestGameServer;
import middleware.utils.TestUser;
import mudgame.controls.actions.CompleteTurn;
import mudgame.server.state.ClassicServerStateSupplier;
import mudgame.server.state.ServerStateSupplier;
import org.junit.jupiter.api.Test;

import java.util.List;

import static middleware.messages_to_client.MessageToClient.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class UserTest {
    private static final ServerStateSupplier serverStateSupplier = new ClassicServerStateSupplier();

    @Test
    void user_receives_room_list() {
        // given
        GameServer server = TestGameServer.create();

        // when
        TestUser user = new TestUser(server);

        // then
        assertThat(user.sent).anyMatch(SetRoomListMessage.class::isInstance);
    }

    @Test
    void user_ids_are_unique_non_null() {
        // given
        GameServer server = TestGameServer.create();

        // when
        TestUser user1 = new TestUser(server);
        TestUser user2 = new TestUser(server);
        TestUser user3 = new TestUser(server);

        // then
        assertThat(List.of(
                user1.user.getUserID(),
                user2.user.getUserID(),
                user3.user.getUserID()
        )).doesNotHaveDuplicates().doesNotContainNull();
    }

    @Test
    void clear_room_should_not_be_called_directly() {
        // given
        GameServer server = TestGameServer.create();
        TestUser user = new TestUser(server);

        // then
        assertThatThrownBy(user.user::clearRoom).isInstanceOf(RuntimeException.class);
    }

    @Test
    void set_room_should_not_be_called_directly() {
        // given
        GameServer server = TestGameServer.create();
        TestUser user = new TestUser(server);
        user.receive().createRoom(new PlayerID(0), 2);
        Room room = user.user.getRoom().orElseThrow();

        // then
        assertThatThrownBy(
                () -> user.user.setRoom(user.user.getRoom().orElseThrow(), new PlayerID(0))
        ).isInstanceOf(RuntimeException.class);
        assertThat(user.user.getRoom().orElseThrow()).isEqualTo(room);
    }

    @Test
    void create_room_with_invalid_size() {
        // given
        GameServer server = TestGameServer.create();
        TestUser user = new TestUser(server);
        user.sent.clear();

        // when
        user.receive().createRoom(new PlayerID(0), -5);

        // then
        assertThat(user.user.getRoom()).isEmpty();
        assertThat(server.getRoomInfoList()).isEmpty();
        assertThat(user.sent).hasSize(1).first().isInstanceOf(ErrorMessage.class);
    }

    @Test
    void create_room_with_invalid_player_id() {
        // given
        GameServer server = TestGameServer.create();
        TestUser user = new TestUser(server);
        user.sent.clear();

        // when
        user.receive().createRoom(new PlayerID(2), 2);

        // then
        assertThat(user.user.getRoom()).isEmpty();
        assertThat(server.getRoomInfoList()).isEmpty();
        assertThat(user.sent).hasSize(1).first().isInstanceOf(ErrorMessage.class);
    }

    @Test
    void create_room_while_in_room() {
        // given
        GameServer server = TestGameServer.create();
        TestUser user = new TestUser(server);
        user.receive().createRoom(new PlayerID(0), 2);
        Room room = user.user.getRoom().orElseThrow();
        user.sent.clear();

        // when
        user.receive().createRoom(new PlayerID(0), 2);

        // then
        assertThat(user.user.getRoom().orElseThrow()).isEqualTo(room);
        assertThat(server.getRoomInfoList()).containsExactly(room.getRoomInfo());
        assertThat(user.sent).anyMatch(ErrorMessage.class::isInstance);
    }

    @Test
    void load_game_with_null_state() {
        // given
        GameServer server = TestGameServer.create();
        TestUser user = new TestUser(server);
        user.sent.clear();

        // when
        user.allowKick();
        user.receive().loadGame(new PlayerID(0), null);

        // then
        assertThat(user.user.getRoom()).isEmpty();
        assertThat(server.getRoomInfoList()).isEmpty();
        assertThat(user.sent).anyMatch(ErrorMessage.class::isInstance);
    }

    @Test
    void load_game_with_invalid_player_id() {
        // given
        GameServer server = TestGameServer.create();
        TestUser user = new TestUser(server);
        user.sent.clear();

        // when
        user.receive().loadGame(new PlayerID(2), serverStateSupplier.get(2));

        // then
        assertThat(user.user.getRoom()).isEmpty();
        assertThat(server.getRoomInfoList()).isEmpty();
        assertThat(user.sent).hasSize(1).first().isInstanceOf(ErrorMessage.class);
    }

    @Test
    void load_game_while_in_room() {
        // given
        GameServer server = TestGameServer.create();
        TestUser user = new TestUser(server);
        user.receive().createRoom(new PlayerID(0), 2);
        Room room = user.user.getRoom().orElseThrow();
        user.sent.clear();

        // when
        user.receive().loadGame(new PlayerID(0), serverStateSupplier.get(2));

        // then
        assertThat(user.user.getRoom().orElseThrow()).isEqualTo(room);
        assertThat(server.getRoomInfoList()).containsExactly(room.getRoomInfo());
        assertThat(user.sent).anyMatch(ErrorMessage.class::isInstance);
    }

    @Test
    void processing_message_after_device_closing_has_no_effect() {
        // given
        GameServer server = TestGameServer.create();
        TestUser user = new TestUser(server);
        user.device.close();

        // when
        user.allowKick();
        user.receive().createRoom(new PlayerID(0), 2);

        // then
        assertThat(server.getUserList()).isEmpty();
        assertThat(server.getRoomInfoList()).isEmpty();
    }

    @Test
    void make_action_while_not_in_room() {
        // given
        GameServer server = TestGameServer.create();
        TestUser user = new TestUser(server);

        // when
        user.receive().makeAction(new CompleteTurn());

        // then
        assertThat(user.sent).anyMatch(ErrorMessage.class::isInstance);
    }

    @Test
    void get_room_list_works() {
        // given
        GameServer server = TestGameServer.create();
        TestUser user = new TestUser(server);
        user.sent.clear();

        // when
        user.receive().getRoomList();

        // then
        assertThat(user.sent).anyMatch(SetRoomListMessage.class::isInstance);
    }

    @Test
    void ping_to_server_works() {
        // given
        GameServer server = TestGameServer.create();
        TestUser user = new TestUser(server);
        user.sent.clear();

        // when
        user.receive().pingToServer();

        // then
        assertThat(user.sent).anyMatch(PongToClientMessage.class::isInstance);
    }

    @Test
    void default_name() {
        // given
        GameServer server = TestGameServer.create();

        // when
        TestUser user = new TestUser(server);

        // then
        assertThat(user.user.getName()).isEqualTo(UserID.DEFAULT_NAME);
    }

    @Test
    void users_can_change_name() {
        // given
        GameServer server = TestGameServer.create();
        TestUser user = new TestUser(server);
        user.sent.clear();

        // when
        user.receive().setName("new_name");

        // then
        assertThat(user.sent)
                .contains(new ChangeNameMessage("new_name"))
                .noneMatch(ErrorMessage.class::isInstance);
    }

    @Test
    void multiple_users_with_same_name_can_coexist() {
        // given
        GameServer server = TestGameServer.create();
        TestUser user1 = new TestUser(server);
        TestUser user2 = new TestUser(server);
        user1.sent.clear();
        user2.sent.clear();

        // when
        user1.receive().setName("new_name");
        user2.receive().setName("new_name");

        // then
        assertThat(user1.sent)
                .contains(new ChangeNameMessage("new_name"))
                .noneMatch(ErrorMessage.class::isInstance);
        assertThat(user2.sent)
                .contains(new ChangeNameMessage("new_name"))
                .noneMatch(ErrorMessage.class::isInstance);

    }

    @Test
    void cannot_download_state_while_not_in_room() {
        // given
        GameServer server = TestGameServer.create();
        TestUser user = new TestUser(server);

        // when
        user.receive().downloadState();

        // then
        assertThat(user.sent)
                .noneMatch(SetDownloadedStateMessage.class::isInstance)
                .anyMatch(ErrorMessage.class::isInstance);
    }

    @Test
    void disconnect_removes_user() {
        // given
        GameServer server = TestGameServer.create();
        TestUser user = new TestUser(server);
        user.receive().createRoom(new PlayerID(0), 2);

        // when
        user.allowKick();
        user.receive().disconnect();

        // then
        assertThat(server.getRoomInfoList()).isEmpty();
        assertThat(server.getUserList()).isEmpty();
    }
}
