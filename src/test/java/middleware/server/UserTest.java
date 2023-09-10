package middleware.server;

import core.model.PlayerID;
import middleware.model.UserID;
import mudgame.controls.actions.CompleteTurn;
import mudgame.server.MudServerCore;
import org.junit.jupiter.api.Test;

import java.util.List;

import static middleware.messages_to_client.MessageToClient.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class UserTest {
    @Test
    void user_receives_id_and_room_list() {
        // given
        GameServer server = new GameServer();

        // when
        TestUser user = new TestUser(server);

        // then
        assertThat(user.sent).hasSize(1);
        assertThat(user.sent.get(0)).isInstanceOf(SetRoomListMessage.class);
    }

    @Test
    void user_ids_are_unique_non_null() {
        // given
        GameServer server = new GameServer();

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
        GameServer server = new GameServer();
        TestUser user = new TestUser(server);

        // then
        assertThatThrownBy(user.user::clearRoom).isInstanceOf(RuntimeException.class);
    }

    @Test
    void set_room_should_not_be_called_directly() {
        // given
        GameServer server = new GameServer();
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
        GameServer server = new GameServer();
        TestUser user = new TestUser(server);
        user.sent.clear();

        // when
        user.receive().createRoom(new PlayerID(0), -5);

        // then
        assertThat(user.user.getRoom()).isEmpty();
        assertThat(server.getRoomList()).isEmpty();
        assertThat(user.sent).hasSize(1).first().isInstanceOf(ErrorMessage.class);
    }

    @Test
    void create_room_with_invalid_player_id() {
        // given
        GameServer server = new GameServer();
        TestUser user = new TestUser(server);
        user.sent.clear();

        // when
        user.receive().createRoom(new PlayerID(2), 2);

        // then
        assertThat(user.user.getRoom()).isEmpty();
        assertThat(server.getRoomList()).isEmpty();
        assertThat(user.sent).hasSize(1).first().isInstanceOf(ErrorMessage.class);
    }

    @Test
    void create_room_while_in_room() {
        // given
        GameServer server = new GameServer();
        TestUser user = new TestUser(server);
        user.receive().createRoom(new PlayerID(0), 2);
        Room room = user.user.getRoom().orElseThrow();
        user.sent.clear();

        // when
        user.receive().createRoom(new PlayerID(0), 2);

        // then
        assertThat(user.user.getRoom().orElseThrow()).isEqualTo(room);
        assertThat(server.getRoomList()).containsExactly(room.getRoomInfo());
        assertThat(user.sent).anyMatch(
                message -> message instanceof ErrorMessage
        );
    }

    @Test
    void load_game_with_null_state() {
        // given
        GameServer server = new GameServer();
        TestUser user = new TestUser(server);
        user.sent.clear();

        // when
        user.receive().loadGame(new PlayerID(0), null);

        // then
        assertThat(user.user.getRoom()).isEmpty();
        assertThat(server.getRoomList()).isEmpty();
        assertThat(user.sent).anyMatch(
                message -> message instanceof ErrorMessage
        );
    }

    @Test
    void load_game_with_invalid_player_id() {
        // given
        GameServer server = new GameServer();
        TestUser user = new TestUser(server);
        user.sent.clear();

        // when
        user.receive().loadGame(new PlayerID(2), MudServerCore.newState(2));

        // then
        assertThat(user.user.getRoom()).isEmpty();
        assertThat(server.getRoomList()).isEmpty();
        assertThat(user.sent).hasSize(1).first().isInstanceOf(ErrorMessage.class);
    }

    @Test
    void load_game_while_in_room() {
        // given
        GameServer server = new GameServer();
        TestUser user = new TestUser(server);
        user.receive().createRoom(new PlayerID(0), 2);
        Room room = user.user.getRoom().orElseThrow();
        user.sent.clear();

        // when
        user.receive().loadGame(new PlayerID(0), MudServerCore.newState(2));

        // then
        assertThat(user.user.getRoom().orElseThrow()).isEqualTo(room);
        assertThat(server.getRoomList()).containsExactly(room.getRoomInfo());
        assertThat(user.sent).anyMatch(
                message -> message instanceof ErrorMessage
        );
    }

    @Test
    void processing_message_after_device_closing_has_no_effect() {
        // given
        GameServer server = new GameServer();
        TestUser user = new TestUser(server);
        user.device.close();

        // when
        user.receive().createRoom(new PlayerID(0), 2);

        // then
        assertThat(server.getConnectedUsers()).isEmpty();
        assertThat(server.getRoomList()).isEmpty();
    }

    @Test
    void make_action_while_not_in_room() {
        // given
        GameServer server = new GameServer();
        TestUser user = new TestUser(server);

        // when
        user.receive().makeAction(new CompleteTurn());

        // then
        assertThat(user.sent).anyMatch(
                message -> message instanceof ErrorMessage
        );
    }

    @Test
    void get_room_list_works() {
        // given
        GameServer server = new GameServer();
        TestUser user = new TestUser(server);
        user.sent.clear();

        // when
        user.receive().getRoomList();

        // then
        assertThat(user.sent).anyMatch(
                message -> message instanceof SetRoomListMessage
        );
    }

    @Test
    void ping_to_server_works() {
        // given
        GameServer server = new GameServer();
        TestUser user = new TestUser(server);
        user.sent.clear();

        // when
        user.receive().pingToServer();

        // then
        assertThat(user.sent).anyMatch(
                message -> message instanceof PongToClientMessage
        );
    }

    @Test
    void default_name() {
        // given
        GameServer server = new GameServer();

        // when
        TestUser user = new TestUser(server);

        // then
        assertThat(user.user.getName()).isEqualTo(UserID.DEFAULT_NAME);
    }

    @Test
    void users_can_change_name() {
        // given
        GameServer server = new GameServer();
        TestUser user = new TestUser(server);
        user.sent.clear();

        // when
        user.receive().setName("new_name");

        // then
        assertThat(user.sent)
                .contains(new ChangeNameMessage("new_name"))
                .noneMatch(message -> message instanceof ErrorMessage);
    }

    @Test
    void multiple_users_with_same_name_can_coexist() {
        // given
        GameServer server = new GameServer();
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
                .noneMatch(message -> message instanceof ErrorMessage);
        assertThat(user2.sent)
                .contains(new ChangeNameMessage("new_name"))
                .noneMatch(message -> message instanceof ErrorMessage);

    }

    @Test
    void cannot_download_state_while_not_in_room() {
        // given
        GameServer server = new GameServer();
        TestUser user = new TestUser(server);

        // when
        user.receive().downloadState();

        // then
        assertThat(user.sent)
                .noneMatch(message -> message instanceof SetDownloadedStateMessage)
                .anyMatch(message -> message instanceof ErrorMessage);
    }
}
