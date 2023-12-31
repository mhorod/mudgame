package middleware.server;

import core.model.PlayerID;
import middleware.model.RoomID;
import middleware.model.RoomInfo;
import middleware.utils.TestGameServer;
import middleware.utils.TestUser;
import mudgame.controls.actions.CompleteTurn;
import mudgame.server.state.ClassicServerStateSupplier;
import mudgame.server.state.ServerStateSupplier;
import org.apache.commons.lang3.SerializationUtils;
import org.junit.jupiter.api.Test;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;

import static middleware.messages_to_client.MessageToClient.*;
import static org.assertj.core.api.Assertions.assertThat;

class RoomTest {
    private final static ServerStateSupplier serverStateSupplier = new ClassicServerStateSupplier();

    @Test
    void users_receive_correct_room_info_on_joining_server() {
        // given
        GameServer server = TestGameServer.create();

        // when
        TestUser user1 = new TestUser(server);
        user1.receive().createRoom(new PlayerID(0), 2);
        TestUser user2 = new TestUser(server);

        // then
        SetRoomListMessage message1 = user1.sent.stream()
                .filter(SetRoomListMessage.class::isInstance)
                .map(SetRoomListMessage.class::cast)
                .findFirst()
                .orElseThrow();
        SetRoomListMessage message2 = user2.sent.stream()
                .filter(SetRoomListMessage.class::isInstance)
                .map(SetRoomListMessage.class::cast)
                .findFirst()
                .orElseThrow();

        assertThat(message1.roomList()).isEmpty();
        assertThat(message2.roomList()).isEqualTo(server.getRoomInfoList());
    }

    @Test
    void users_receive_room_info_on_creating_room() {
        // given
        GameServer server = TestGameServer.create();
        TestUser user = new TestUser(server);
        user.sent.clear();

        // when
        user.receive().createRoom(new PlayerID(0), 2);

        // then
        SetCurrentRoomMessage message = (SetCurrentRoomMessage) user.sent.get(0);
        assertThat(message.roomInfo()).isEqualTo(server.getRoomInfoList().get(0));
    }

    @Test
    void users_receive_room_info_on_loading_game() {
        // given
        GameServer server = TestGameServer.create();
        TestUser user = new TestUser(server);
        user.sent.clear();

        // when
        user.receive().loadGame(new PlayerID(0), serverStateSupplier.get(2));

        // then
        SetCurrentRoomMessage message = (SetCurrentRoomMessage) user.sent.get(0);
        assertThat(message.roomInfo()).isEqualTo(server.getRoomInfoList().get(0));
    }

    @Test
    void users_receive_updated_room_info_on_others_joining_game() {
        // given
        GameServer server = TestGameServer.create();
        TestUser user1 = new TestUser(server);
        user1.sent.clear();
        TestUser user2 = new TestUser(server);

        // when
        user1.receive().createRoom(new PlayerID(0), 2);
        RoomID roomID = ((SetCurrentRoomMessage) user1.sent.get(0)).roomInfo().roomID();
        user1.sent.clear();
        user2.receive().joinRoom(new PlayerID(1), roomID);

        // then
        SetCurrentRoomMessage message = (SetCurrentRoomMessage) user1.sent.get(0);
        assertThat(message.roomInfo()).isEqualTo(server.getRoomInfoList().get(0));
    }

    @Test
    void room_info_after_creating_is_correct() {
        // given
        GameServer server = TestGameServer.create();
        TestUser user = new TestUser(server, "user");

        // when
        user.receive().createRoom(new PlayerID(1), 2);

        // then
        assertThat(server.getRoomInfoList()).hasSize(1).first()
                .matches(info -> info.roomID() != null)
                .matches(info -> info.owner().equals("user"))
                .matches(info -> !info.isRunning())
                .matches(info -> info.players().equals(new HashMap<>() {{
                    put(new PlayerID(0), null);
                    put(new PlayerID(1), "user");
                }}));
    }

    @Test
    void room_info_after_loading_is_correct() {
        // given
        GameServer server = TestGameServer.create();
        TestUser user = new TestUser(server, "user");

        // when
        user.receive().loadGame(new PlayerID(1), serverStateSupplier.get(2));

        // then
        assertThat(server.getRoomInfoList()).hasSize(1).first()
                .matches(info -> info.roomID() != null)
                .matches(info -> info.owner().equals("user"))
                .matches(info -> !info.isRunning())
                .matches(info -> info.players().equals(new HashMap<>() {{
                    put(new PlayerID(0), null);
                    put(new PlayerID(1), "user");
                }}));
    }

    @Test
    void room_info_after_joining_is_correct() {
        // given
        GameServer server = TestGameServer.create();
        TestUser user1 = new TestUser(server, "user1");
        user1.receive().createRoom(new PlayerID(1), 2);
        TestUser user2 = new TestUser(server, "user2");

        // when
        user2.receive().joinRoom(new PlayerID(0), server.getRoomInfoList().get(0).roomID());

        // then
        assertThat(server.getRoomInfoList()).hasSize(1).first()
                .matches(info -> info.roomID() != null)
                .matches(info -> info.owner().equals("user1"))
                .matches(info -> !info.isRunning())
                .matches(info -> info.players().equals(new HashMap<>() {{
                    put(new PlayerID(0), "user2");
                    put(new PlayerID(1), "user1");
                }}));
    }

    @Test
    void room_info_after_joining_and_owner_leaving_is_correct() {
        // given
        GameServer server = TestGameServer.create();
        TestUser user1 = new TestUser(server, "user1");
        user1.receive().createRoom(new PlayerID(1), 2);
        TestUser user2 = new TestUser(server, "user2");
        user2.receive().joinRoom(new PlayerID(0), server.getRoomInfoList().get(0).roomID());

        // when
        user1.receive().leaveRoom();

        // then
        assertThat(server.getRoomInfoList()).hasSize(1).first()
                .matches(info -> info.roomID() != null)
                .matches(info -> info.owner().equals("user2"))
                .matches(info -> !info.isRunning())
                .matches(info -> info.players().equals(new HashMap<>() {{
                    put(new PlayerID(0), "user2");
                    put(new PlayerID(1), null);
                }}));
    }

    @Test
    void room_info_after_joining_and_other_leaving_is_correct() {
        // given
        GameServer server = TestGameServer.create();
        TestUser user1 = new TestUser(server, "user1");
        user1.receive().createRoom(new PlayerID(1), 2);
        TestUser user2 = new TestUser(server, "user2");
        user2.receive().joinRoom(new PlayerID(0), server.getRoomInfoList().get(0).roomID());

        // when
        user2.receive().leaveRoom();

        // then
        assertThat(server.getRoomInfoList()).hasSize(1).first()
                .matches(info -> info.roomID() != null)
                .matches(info -> info.owner().equals("user1"))
                .matches(info -> !info.isRunning())
                .matches(info -> info.players().equals(new HashMap<>() {{
                    put(new PlayerID(0), null);
                    put(new PlayerID(1), "user1");
                }}));
    }

    @Test
    void room_info_after_starting_is_correct() {
        // given
        GameServer server = TestGameServer.create();
        TestUser user1 = new TestUser(server, "user1");
        user1.receive().createRoom(new PlayerID(1), 2);

        TestUser user2 = new TestUser(server, "user2");
        user2.receive().joinRoom(new PlayerID(0), user1.user.getRoom().orElseThrow().getRoomID());

        // when
        user1.receive().startGame();

        // then
        assertThat(server.getRoomInfoList()).hasSize(1).first()
                .matches(info -> info.roomID() != null)
                .matches(info -> info.owner().equals("user1"))
                .matches(RoomInfo::isRunning)
                .matches(info -> info.players().equals(new HashMap<>() {{
                    put(new PlayerID(0), "user2");
                    put(new PlayerID(1), "user1");
                }}));
    }

    @Test
    void room_is_deleted_after_everyone_leaves() {
        // given
        GameServer server = TestGameServer.create();
        TestUser user1 = new TestUser(server);
        user1.receive().createRoom(new PlayerID(1), 2);
        TestUser user2 = new TestUser(server);
        user2.receive().joinRoom(new PlayerID(0), server.getRoomInfoList().get(0).roomID());

        // when
        user1.receive().leaveRoom();
        user2.receive().leaveRoom();

        // then
        assertThat(server.getRoomInfoList()).isEmpty();
    }

    @Test
    void room_info_is_serializable() {
        // given
        GameServer server = TestGameServer.create();
        TestUser user1 = new TestUser(server);
        user1.receive().createRoom(new PlayerID(1), 2);

        // when
        List<RoomInfo> serialized = SerializationUtils.clone(
                (Serializable & List<RoomInfo>) server.getRoomInfoList());

        // then
        assertThat(serialized).isEqualTo(server.getRoomInfoList());
    }

    @Test
    void non_owner_cannot_start_game() {
        // given
        GameServer server = TestGameServer.create();
        TestUser user1 = new TestUser(server, "user1");
        user1.receive().createRoom(new PlayerID(1), 2);
        TestUser user2 = new TestUser(server, "user2");
        user2.receive().joinRoom(new PlayerID(0), server.getRoomInfoList().get(0).roomID());

        user1.sent.clear();
        user2.sent.clear();

        // when
        user2.receive().startGame();

        // then
        assertThat(server.getRoomInfoList()).hasSize(1).first()
                .matches(info -> info.roomID() != null)
                .matches(info -> info.owner().equals("user1"))
                .matches(info -> !info.isRunning())
                .matches(info -> info.players().equals(new HashMap<>() {{
                    put(new PlayerID(0), "user2");
                    put(new PlayerID(1), "user1");
                }}));
        assertThat(user1.sent).isEmpty();
        assertThat(user2.sent).hasSize(1).first().isInstanceOf(ErrorMessage.class);
    }

    @Test
    void second_start_results_in_error() {
        // given
        GameServer server = TestGameServer.create();
        TestUser user1 = new TestUser(server);
        user1.receive().createRoom(new PlayerID(1), 2);
        TestUser user2 = new TestUser(server);
        user2.receive().joinRoom(new PlayerID(0), server.getRoomInfoList().get(0).roomID());

        user1.receive().startGame();
        user1.sent.clear();
        user2.sent.clear();

        // when
        user1.receive().startGame();

        // then
        assertThat(user1.user.getRoom().orElseThrow().getRoomInfo().isRunning()).isTrue();
        assertThat(user1.sent).hasSize(1).first().isInstanceOf(ErrorMessage.class);
        assertThat(user2.sent).isEmpty();
    }

    @Test
    void joining_game_using_duplicate_player_id() {
        // given
        GameServer server = TestGameServer.create();
        TestUser user1 = new TestUser(server, "user1");
        user1.receive().createRoom(new PlayerID(1), 2);
        TestUser user2 = new TestUser(server, "user2");
        user2.sent.clear();

        // when
        user2.receive().joinRoom(new PlayerID(1), server.getRoomInfoList().get(0).roomID());

        // then
        assertThat(server.getRoomInfoList()).hasSize(1).first()
                .matches(info -> info.roomID() != null)
                .matches(info -> info.owner().equals("user1"))
                .matches(info -> !info.isRunning())
                .matches(info -> info.players().equals(new HashMap<>() {{
                    put(new PlayerID(0), null);
                    put(new PlayerID(1), "user1");
                }}));
        assertThat(user2.sent).hasSize(1).first().isInstanceOf(ErrorMessage.class);
    }

    @Test
    void joining_game_using_out_of_range_player_id() {
        // given
        GameServer server = TestGameServer.create();
        TestUser user1 = new TestUser(server, "user1");
        user1.receive().createRoom(new PlayerID(1), 2);
        TestUser user2 = new TestUser(server, "user2");
        user2.sent.clear();

        // when
        user2.receive().joinRoom(new PlayerID(123456), server.getRoomInfoList().get(0).roomID());

        // then
        assertThat(server.getRoomInfoList()).hasSize(1).first()
                .matches(info -> info.roomID() != null)
                .matches(info -> info.owner().equals("user1"))
                .matches(info -> !info.isRunning())
                .matches(info -> info.players().equals(new HashMap<>() {{
                    put(new PlayerID(0), null);
                    put(new PlayerID(1), "user1");
                }}));
        assertThat(user2.sent).hasSize(1).first().isInstanceOf(ErrorMessage.class);
    }

    @Test
    void joining_game_using_null_player_id() {
        // given
        GameServer server = TestGameServer.create();
        TestUser user1 = new TestUser(server, "user1");
        user1.receive().createRoom(new PlayerID(1), 2);
        TestUser user2 = new TestUser(server, "user2");
        user2.sent.clear();

        // when
        user2.receive().joinRoom(null, server.getRoomInfoList().get(0).roomID());

        // then
        assertThat(server.getRoomInfoList()).hasSize(1).first()
                .matches(info -> info.roomID() != null)
                .matches(info -> info.owner().equals("user1"))
                .matches(info -> !info.isRunning())
                .matches(info -> info.players().equals(new HashMap<>() {{
                    put(new PlayerID(0), null);
                    put(new PlayerID(1), "user1");
                }}));
        assertThat(user2.sent).hasSize(1).first().isInstanceOf(ErrorMessage.class);
    }

    @Test
    void joining_game_using_null_room_id() {
        // given
        GameServer server = TestGameServer.create();
        TestUser user1 = new TestUser(server, "user1");
        user1.receive().createRoom(new PlayerID(1), 2);
        TestUser user2 = new TestUser(server, "user2");
        user2.sent.clear();

        // when
        user2.receive().joinRoom(new PlayerID(0), null);

        // then
        assertThat(server.getRoomInfoList()).hasSize(1).first()
                .matches(info -> info.roomID() != null)
                .matches(info -> info.owner().equals("user1"))
                .matches(info -> !info.isRunning())
                .matches(info -> info.players().equals(new HashMap<>() {{
                    put(new PlayerID(0), null);
                    put(new PlayerID(1), "user1");
                }}));
        assertThat(user2.sent).anyMatch(ErrorMessage.class::isInstance);
    }

    @Test
    void joining_game_using_invalid_room_id() {
        // given
        GameServer server = TestGameServer.create();
        TestUser user1 = new TestUser(server, "user1");
        user1.receive().createRoom(new PlayerID(1), 2);
        TestUser user2 = new TestUser(server, "user2");
        user2.sent.clear();

        // when
        user2.receive().joinRoom(new PlayerID(0), new RoomID(-789));

        // then
        assertThat(server.getRoomInfoList()).hasSize(1).first()
                .matches(info -> info.roomID() != null)
                .matches(info -> info.owner().equals("user1"))
                .matches(info -> !info.isRunning())
                .matches(info -> info.players().equals(new HashMap<>() {{
                    put(new PlayerID(0), null);
                    put(new PlayerID(1), "user1");
                }}));
        assertThat(user2.sent).anyMatch(ErrorMessage.class::isInstance);
        assertThat(user2.user.getRoom()).isEmpty();
        assertThat(user2.user.getPlayerID()).isEmpty();
    }

    @Test
    void sending_events_before_start_does_not_work() {
        // given
        GameServer server = TestGameServer.create();

        TestUser user1 = new TestUser(server);
        user1.receive().createRoom(new PlayerID(0), 2);

        TestUser user2 = new TestUser(server);
        user2.receive().joinRoom(new PlayerID(1), user1.user.getRoom().orElseThrow().getRoomID());

        user1.sent.clear();
        user2.sent.clear();

        // when
        user1.receive().makeAction(new CompleteTurn());

        // then
        assertThat(user1.sent).hasSize(1).first().isInstanceOf(ErrorMessage.class);
        assertThat(user2.sent).isEmpty();
    }

    @Test
    void sending_events_after_start_works() {
        // given
        GameServer server = TestGameServer.create();

        TestUser user1 = new TestUser(server);
        user1.receive().createRoom(new PlayerID(0), 2);

        TestUser user2 = new TestUser(server);
        user2.receive().joinRoom(new PlayerID(1), user1.user.getRoom().orElseThrow().getRoomID());

        user1.receive().startGame();

        user1.sent.clear();
        user2.sent.clear();

        // when
        user1.receive().makeAction(new CompleteTurn());

        // then
        assertThat(user1.sent).isNotEmpty().allMatch(RegisterEventMessage.class::isInstance);
        assertThat(user2.sent).isNotEmpty().allMatch(RegisterEventMessage.class::isInstance);
    }

    @Test
    void sending_null_events_is_handled_correctly() {
        // given
        GameServer server = TestGameServer.create();

        TestUser user1 = new TestUser(server);
        user1.receive().createRoom(new PlayerID(0), 2);

        TestUser user2 = new TestUser(server);
        user2.receive().joinRoom(new PlayerID(1), user1.user.getRoom().orElseThrow().getRoomID());

        user1.receive().startGame();

        user1.sent.clear();
        user2.sent.clear();

        // when
        user1.receive().makeAction(null);

        // then
        assertThat(user2.sent).isEmpty();
    }

    @Test
    void users_receive_updated_room_info_and_state_after_start() {
        // given
        GameServer server = TestGameServer.create();

        TestUser user1 = new TestUser(server);
        user1.receive().createRoom(new PlayerID(0), 2);

        TestUser user2 = new TestUser(server);
        user2.receive().joinRoom(new PlayerID(1), user1.user.getRoom().orElseThrow().getRoomID());

        user1.sent.clear();
        user2.sent.clear();

        // when
        user1.receive().startGame();

        // then
        assertThat(user1.sent)
                .anyMatch(SetCurrentRoomMessage.class::isInstance)
                .anyMatch(SetGameStateMessage.class::isInstance);
        assertThat(user2.sent)
                .anyMatch(SetCurrentRoomMessage.class::isInstance)
                .anyMatch(SetGameStateMessage.class::isInstance);
    }

    @Test
    void users_receive_room_info_and_state_after_hot_joining() {
        // given
        GameServer server = TestGameServer.create();

        TestUser user1 = new TestUser(server);
        user1.receive().createRoom(new PlayerID(0), 2);

        TestUser user2 = new TestUser(server);
        user2.receive().joinRoom(new PlayerID(1), user1.user.getRoom().orElseThrow().getRoomID());

        user1.receive().startGame();
        user2.receive().leaveRoom();

        TestUser user3 = new TestUser(server);

        user1.sent.clear();
        user3.sent.clear();

        // when
        user3.receive().joinRoom(new PlayerID(1), user1.user.getRoom().orElseThrow().getRoomID());

        // then
        assertThat(user3.sent)
                .anyMatch(SetCurrentRoomMessage.class::isInstance)
                .anyMatch(SetGameStateMessage.class::isInstance);
    }

    @Test
    void users_receive_updated_room_info_after_someone_hot_joins() {
        // given
        GameServer server = TestGameServer.create();

        TestUser user1 = new TestUser(server);
        user1.receive().createRoom(new PlayerID(0), 2);

        TestUser user2 = new TestUser(server);
        user2.receive().joinRoom(new PlayerID(1), user1.user.getRoom().orElseThrow().getRoomID());

        user1.receive().startGame();
        user2.receive().leaveRoom();

        TestUser user3 = new TestUser(server);

        user1.sent.clear();
        user3.sent.clear();

        // when
        user3.receive().joinRoom(new PlayerID(1), user1.user.getRoom().orElseThrow().getRoomID());

        // then
        SetCurrentRoomMessage message = user1.sent.stream()
                .filter(SetCurrentRoomMessage.class::isInstance)
                .map(SetCurrentRoomMessage.class::cast)
                .findFirst().orElseThrow();
        assertThat(message.roomInfo()).isEqualTo(user1.user.getRoom().orElseThrow().getRoomInfo());
        assertThat(message.isOwner()).isTrue();
        assertThat(message.myPlayerID()).isEqualTo(new PlayerID(0));
    }

    @Test
    void room_info_after_hot_join_is_correct() {
        // given
        GameServer server = TestGameServer.create();

        TestUser user1 = new TestUser(server, "user1");
        user1.receive().createRoom(new PlayerID(0), 2);

        TestUser user2 = new TestUser(server, "user2");
        user2.receive().joinRoom(new PlayerID(1), user1.user.getRoom().orElseThrow().getRoomID());

        user1.receive().startGame();
        user2.receive().leaveRoom();

        TestUser user3 = new TestUser(server, "user3");

        // when
        user3.receive().joinRoom(new PlayerID(1), user1.user.getRoom().orElseThrow().getRoomID());

        // then
        assertThat(server.getRoomInfoList()).hasSize(1).first()
                .matches(info -> info.roomID() != null)
                .matches(info -> info.owner().equals("user1"))
                .matches(RoomInfo::isRunning)
                .matches(info -> info.players().equals(new HashMap<>() {{
                    put(new PlayerID(0), "user1");
                    put(new PlayerID(1), "user3");
                }}));
    }

    @Test
    void client_game_state_is_deeply_copied() {
        // given
        GameServer server = TestGameServer.create();

        TestUser user1 = new TestUser(server);
        user1.receive().createRoom(new PlayerID(0), 2);

        TestUser user2 = new TestUser(server);
        user2.receive().joinRoom(new PlayerID(1), user1.user.getRoom().orElseThrow().getRoomID());

        // when
        user1.receive().startGame();
        user1.receive().makeAction(new CompleteTurn());

        // then
        List<SetGameStateMessage> setGameStateMessages = user1.sent.stream()
                .filter(SetGameStateMessage.class::isInstance)
                .map(SetGameStateMessage.class::cast).toList();
        assertThat(setGameStateMessages).hasSize(1).first().matches(
                m -> m.state().turnManager().isMyTurn()
        );
    }

    @Test
    void room_info_after_username_change_is_correct() {
        // given
        GameServer server = TestGameServer.create();

        TestUser user1 = new TestUser(server, "user1");
        user1.receive().createRoom(new PlayerID(0), 2);
        TestUser user2 = new TestUser(server, "user2");
        user2.receive().joinRoom(new PlayerID(1), user1.user.getRoom().orElseThrow().getRoomID());

        // when
        user1.receive().setName("new_name");

        // then
        assertThat(server.getRoomInfoList()).hasSize(1).first()
                .matches(info -> info.roomID() != null)
                .matches(info -> info.owner().equals("new_name"))
                .matches(info -> !info.isRunning())
                .matches(info -> info.players().equals(new HashMap<>() {{
                    put(new PlayerID(0), "new_name");
                    put(new PlayerID(1), "user2");
                }}));
    }

    @Test
    void room_info_is_send_after_someone_changes_username() {
        // given
        GameServer server = TestGameServer.create();

        TestUser user1 = new TestUser(server, "user1");
        user1.receive().createRoom(new PlayerID(0), 2);
        TestUser user2 = new TestUser(server, "user2");
        user2.receive().joinRoom(new PlayerID(1), user1.user.getRoom().orElseThrow().getRoomID());

        user1.sent.clear();
        user2.sent.clear();

        // when
        user1.receive().setName("new_name");

        // then
        assertThat(user1.sent)
                .anyMatch(SetCurrentRoomMessage.class::isInstance);
        assertThat(user2.sent)
                .anyMatch(SetCurrentRoomMessage.class::isInstance);
    }

    @Test
    void owner_can_download_state() {
        // given
        GameServer server = TestGameServer.create();

        TestUser user1 = new TestUser(server, "user1");
        user1.receive().createRoom(new PlayerID(0), 2);
        TestUser user2 = new TestUser(server, "user2");
        user2.receive().joinRoom(new PlayerID(1), user1.user.getRoom().orElseThrow().getRoomID());

        user1.sent.clear();
        user2.sent.clear();

        // when
        user1.receive().downloadState();

        // then
        assertThat(user1.sent)
                .anyMatch(SetDownloadedStateMessage.class::isInstance)
                .noneMatch(ErrorMessage.class::isInstance);
        assertThat(user2.sent)
                .noneMatch(SetDownloadedStateMessage.class::isInstance);
    }

    @Test
    void non_owner_cannot_download_state() {
        // given
        GameServer server = TestGameServer.create();

        TestUser user1 = new TestUser(server, "user1");
        user1.receive().createRoom(new PlayerID(0), 2);
        TestUser user2 = new TestUser(server, "user2");
        user2.receive().joinRoom(new PlayerID(1), user1.user.getRoom().orElseThrow().getRoomID());

        // when
        user2.receive().downloadState();

        // then
        assertThat(user1.sent)
                .noneMatch(SetDownloadedStateMessage.class::isInstance);
        assertThat(user2.sent)
                .noneMatch(SetDownloadedStateMessage.class::isInstance)
                .anyMatch(ErrorMessage.class::isInstance);
    }

    @Test
    void downloaded_server_state_is_deeply_copied() {
        // given
        GameServer server = TestGameServer.create();

        TestUser user1 = new TestUser(server);
        user1.receive().createRoom(new PlayerID(0), 2);

        TestUser user2 = new TestUser(server);
        user2.receive().joinRoom(new PlayerID(1), user1.user.getRoom().orElseThrow().getRoomID());

        user1.receive().startGame();

        user1.sent.clear();
        user1.receive().downloadState();
        SetDownloadedStateMessage message1 = (SetDownloadedStateMessage) user1.sent.get(0);

        // when
        user1.receive().makeAction(new CompleteTurn());
        user1.sent.clear();
        user1.receive().downloadState();
        SetDownloadedStateMessage message2 = (SetDownloadedStateMessage) user1.sent.get(0);

        // then
        assertThat(message1.state().turnManager().currentPlayer()).isNotEqualTo(
                message2.state().turnManager().currentPlayer());
    }

    @Test
    void room_that_is_not_full_cannot_be_started() {
        // given
        GameServer server = TestGameServer.create();

        TestUser user1 = new TestUser(server, "user1");
        user1.receive().createRoom(new PlayerID(0), 3);

        TestUser user2 = new TestUser(server, "user2");
        user2.receive().joinRoom(new PlayerID(1), user1.user.getRoom().orElseThrow().getRoomID());

        user1.sent.clear();
        user2.sent.clear();

        // when
        user1.receive().startGame();

        // when
        assertThat(server.getRoomInfoList()).hasSize(1).first()
                .matches(info -> info.roomID() != null)
                .matches(info -> info.owner().equals("user1"))
                .matches(info -> !info.isRunning())
                .matches(info -> info.players().equals(new HashMap<>() {{
                    put(new PlayerID(0), "user1");
                    put(new PlayerID(1), "user2");
                    put(new PlayerID(2), null);
                }}));
        assertThat(user1.sent).anyMatch(ErrorMessage.class::isInstance);
        assertThat(user2.sent).isEmpty();
    }

    @Test
    void room_list_is_sent_when_someone_creates_room() {
        // given
        GameServer server = TestGameServer.create();

        TestUser user1 = new TestUser(server, "user1");
        TestUser user2 = new TestUser(server, "user2");

        user1.sent.clear();

        // when
        user2.receive().createRoom(new PlayerID(0), 2);

        // then
        assertThat(new SetRoomListMessage(server.getRoomInfoList())).isIn(user1.sent);
    }

    @Test
    void room_list_is_sent_when_room_is_deleted() {
        // given
        GameServer server = TestGameServer.create();

        TestUser user1 = new TestUser(server, "user1");
        TestUser user2 = new TestUser(server, "user2");

        user2.receive().createRoom(new PlayerID(0), 2);
        user1.sent.clear();

        // when
        user2.receive().leaveRoom();

        // then
        assertThat(new SetRoomListMessage(server.getRoomInfoList())).isIn(user1.sent);
    }

    @Test
    void room_list_is_sent_when_someone_joins_room() {
        // given
        GameServer server = TestGameServer.create();

        TestUser user1 = new TestUser(server, "user1");
        TestUser user2 = new TestUser(server, "user2");
        TestUser user3 = new TestUser(server, "user3");

        user2.receive().createRoom(new PlayerID(0), 2);
        user1.sent.clear();

        // when
        user3.receive().joinRoom(new PlayerID(1), user2.user.getRoom().orElseThrow().getRoomID());

        // then
        assertThat(new SetRoomListMessage(server.getRoomInfoList())).isIn(user1.sent);
    }

    @Test
    void room_list_is_sent_when_someone_leaves_room() {
        // given
        GameServer server = TestGameServer.create();

        TestUser user1 = new TestUser(server, "user1");
        TestUser user2 = new TestUser(server, "user2");
        TestUser user3 = new TestUser(server, "user3");

        user2.receive().createRoom(new PlayerID(0), 2);
        user3.receive().joinRoom(new PlayerID(1), user2.user.getRoom().orElseThrow().getRoomID());
        user1.sent.clear();

        // when
        user2.receive().leaveRoom();

        // then
        assertThat(new SetRoomListMessage(server.getRoomInfoList())).isIn(user1.sent);
    }

    @Test
    void room_list_is_sent_when_someone_starts_game() {
        // given
        GameServer server = TestGameServer.create();

        TestUser user1 = new TestUser(server, "user1");
        TestUser user2 = new TestUser(server, "user2");
        TestUser user3 = new TestUser(server, "user3");

        user2.receive().createRoom(new PlayerID(0), 2);
        user3.receive().joinRoom(new PlayerID(1), user2.user.getRoom().orElseThrow().getRoomID());
        user1.sent.clear();

        // when
        user2.receive().startGame();

        // then
        assertThat(user2.user.getRoom().orElseThrow().getRoomInfo().isRunning()).isTrue();
        assertThat(new SetRoomListMessage(server.getRoomInfoList())).isIn(user1.sent);
    }

    @Test
    void user_leaving_room_receives_set_current_room_if_room_is_deleted() {
        // given
        GameServer server = TestGameServer.create();

        TestUser user1 = new TestUser(server, "user1");
        user1.receive().createRoom(new PlayerID(0), 2);
        user1.sent.clear();

        // when
        user1.receive().leaveRoom();

        // when
        assertThat(new SetCurrentRoomMessage(null, false, null)).isIn(user1.sent);
    }

    @Test
    void user_leaving_room_receives_set_current_room() {
        // given
        GameServer server = TestGameServer.create();

        TestUser user1 = new TestUser(server, "user1");
        TestUser user2 = new TestUser(server, "user1");

        user1.receive().createRoom(new PlayerID(0), 2);
        user2.receive().joinRoom(new PlayerID(1), user1.user.getRoom().orElseThrow().getRoomID());
        user1.sent.clear();

        // when
        user1.receive().leaveRoom();

        // when
        assertThat(new SetCurrentRoomMessage(null, false, null)).isIn(user1.sent);
    }
}
