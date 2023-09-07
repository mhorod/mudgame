package middleware.server;

import core.model.PlayerID;
import core.turns.CompleteTurn;
import middleware.model.RoomID;
import middleware.model.RoomInfo;
import mudgame.server.MudServerCore;
import org.apache.commons.lang3.SerializationUtils;
import org.junit.jupiter.api.Test;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;

import static middleware.messages_to_client.MessageToClient.*;
import static org.assertj.core.api.Assertions.assertThat;

public class RoomTest {
    @Test
    void users_receive_correct_room_info_on_joining_server() {
        // given
        GameServer server = new GameServer();

        // when
        TestUser user1 = new TestUser(server);
        user1.receive().createRoom(new PlayerID(0), 2);
        TestUser user2 = new TestUser(server);

        // then
        SetRoomListMessage message1 = (SetRoomListMessage) user1.sent.get(1);
        SetRoomListMessage message2 = (SetRoomListMessage) user2.sent.get(1);

        assertThat(message1.roomList()).isEmpty();
        assertThat(message2.roomList()).isEqualTo(server.getRoomList());
    }

    @Test
    void users_receive_room_info_on_creating_room() {
        // given
        GameServer server = new GameServer();
        TestUser user = new TestUser(server);
        user.sent.clear();

        // when
        user.receive().createRoom(new PlayerID(0), 2);

        // then
        SetCurrentRoomMessage message = (SetCurrentRoomMessage) user.sent.get(0);
        assertThat(message.roomInfo()).isEqualTo(server.getRoomList().get(0));
    }

    @Test
    void users_receive_room_info_on_loading_game() {
        // given
        GameServer server = new GameServer();
        TestUser user = new TestUser(server);
        user.sent.clear();

        // when
        user.receive().loadGame(new PlayerID(0), MudServerCore.newState(2));

        // then
        SetCurrentRoomMessage message = (SetCurrentRoomMessage) user.sent.get(0);
        assertThat(message.roomInfo()).isEqualTo(server.getRoomList().get(0));
    }

    @Test
    void users_receive_updated_room_info_on_others_joining_game() {
        // given
        GameServer server = new GameServer();
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
        assertThat(message.roomInfo()).isEqualTo(server.getRoomList().get(0));
    }

    @Test
    void room_info_after_creating_is_correct() {
        // given
        GameServer server = new GameServer();
        TestUser user = new TestUser(server);

        // when
        user.receive().createRoom(new PlayerID(1), 2);

        // then
        assertThat(server.getRoomList()).hasSize(1).first()
                .matches(info -> info.roomID() != null)
                .matches(info -> info.owner().equals(user.user.getUserID()))
                .matches(info -> !info.isRunning())
                .matches(info -> info.players().equals(new HashMap<>() {{
                    put(new PlayerID(0), null);
                    put(new PlayerID(1), user.user.getUserID());
                }}));
    }

    @Test
    void room_info_after_loading_is_correct() {
        // given
        GameServer server = new GameServer();
        TestUser user = new TestUser(server);

        // when
        user.receive().loadGame(new PlayerID(1), MudServerCore.newState(2));

        // then
        assertThat(server.getRoomList()).hasSize(1).first()
                .matches(info -> info.roomID() != null)
                .matches(info -> info.owner().equals(user.user.getUserID()))
                .matches(info -> !info.isRunning())
                .matches(info -> info.players().equals(new HashMap<>() {{
                    put(new PlayerID(0), null);
                    put(new PlayerID(1), user.user.getUserID());
                }}));
    }

    @Test
    void room_info_after_joining_is_correct() {
        // given
        GameServer server = new GameServer();
        TestUser user1 = new TestUser(server);
        user1.receive().createRoom(new PlayerID(1), 2);
        TestUser user2 = new TestUser(server);

        // when
        user2.receive().joinRoom(new PlayerID(0), server.getRoomList().get(0).roomID());

        // then
        assertThat(server.getRoomList()).hasSize(1).first()
                .matches(info -> info.roomID() != null)
                .matches(info -> info.owner().equals(user1.user.getUserID()))
                .matches(info -> !info.isRunning())
                .matches(info -> info.players().equals(new HashMap<>() {{
                    put(new PlayerID(0), user2.user.getUserID());
                    put(new PlayerID(1), user1.user.getUserID());
                }}));
    }

    @Test
    void room_info_after_joining_and_owner_leaving_is_correct() {
        // given
        GameServer server = new GameServer();
        TestUser user1 = new TestUser(server);
        user1.receive().createRoom(new PlayerID(1), 2);
        TestUser user2 = new TestUser(server);
        user2.receive().joinRoom(new PlayerID(0), server.getRoomList().get(0).roomID());

        // when
        user1.receive().leaveRoom();

        // then
        assertThat(server.getRoomList()).hasSize(1).first()
                .matches(info -> info.roomID() != null)
                .matches(info -> info.owner().equals(user2.user.getUserID()))
                .matches(info -> !info.isRunning())
                .matches(info -> info.players().equals(new HashMap<>() {{
                    put(new PlayerID(0), user2.user.getUserID());
                    put(new PlayerID(1), null);
                }}));
    }

    @Test
    void room_info_after_joining_and_other_leaving_is_correct() {
        // given
        GameServer server = new GameServer();
        TestUser user1 = new TestUser(server);
        user1.receive().createRoom(new PlayerID(1), 2);
        TestUser user2 = new TestUser(server);
        user2.receive().joinRoom(new PlayerID(0), server.getRoomList().get(0).roomID());

        // when
        user2.receive().leaveRoom();

        // then
        assertThat(server.getRoomList()).hasSize(1).first()
                .matches(info -> info.roomID() != null)
                .matches(info -> info.owner().equals(user1.user.getUserID()))
                .matches(info -> !info.isRunning())
                .matches(info -> info.players().equals(new HashMap<>() {{
                    put(new PlayerID(0), null);
                    put(new PlayerID(1), user1.user.getUserID());
                }}));
    }

    @Test
    void room_info_after_starting_is_correct() {
        // given
        GameServer server = new GameServer();
        TestUser user = new TestUser(server);
        user.receive().createRoom(new PlayerID(1), 2);

        // when
        user.receive().startGame();

        // then
        assertThat(server.getRoomList()).hasSize(1).first()
                .matches(info -> info.roomID() != null)
                .matches(info -> info.owner().equals(user.user.getUserID()))
                .matches(RoomInfo::isRunning)
                .matches(info -> info.players().equals(new HashMap<>() {{
                    put(new PlayerID(0), null);
                    put(new PlayerID(1), user.user.getUserID());
                }}));
    }

    @Test
    void room_is_deleted_after_everyone_leaves() {
        // given
        GameServer server = new GameServer();
        TestUser user1 = new TestUser(server);
        user1.receive().createRoom(new PlayerID(1), 2);
        TestUser user2 = new TestUser(server);
        user2.receive().joinRoom(new PlayerID(0), server.getRoomList().get(0).roomID());

        // when
        user1.receive().leaveRoom();
        user2.receive().leaveRoom();

        // then
        assertThat(server.getRoomList()).isEmpty();
    }

    @Test
    void room_info_is_serializable() {
        // given
        GameServer server = new GameServer();
        TestUser user1 = new TestUser(server);
        user1.receive().createRoom(new PlayerID(1), 2);

        // when
        List<RoomInfo> serialized = SerializationUtils.clone((Serializable & List<RoomInfo>) server.getRoomList());

        // then
        assertThat(serialized).isEqualTo(server.getRoomList());
    }

    @Test
    void non_owner_cannot_start_game() {
        // given
        GameServer server = new GameServer();
        TestUser user1 = new TestUser(server);
        user1.receive().createRoom(new PlayerID(1), 2);
        TestUser user2 = new TestUser(server);
        user2.receive().joinRoom(new PlayerID(0), server.getRoomList().get(0).roomID());

        user1.sent.clear();
        user2.sent.clear();

        // when
        user2.receive().startGame();

        // then
        assertThat(server.getRoomList()).hasSize(1).first()
                .matches(info -> info.roomID() != null)
                .matches(info -> info.owner().equals(user1.user.getUserID()))
                .matches(info -> !info.isRunning())
                .matches(info -> info.players().equals(new HashMap<>() {{
                    put(new PlayerID(0), user2.user.getUserID());
                    put(new PlayerID(1), user1.user.getUserID());
                }}));
        assertThat(user1.sent).isEmpty();
        assertThat(user2.sent).hasSize(1).first().isInstanceOf(ErrorMessage.class);
    }

    @Test
    void second_start_results_in_error() {
        // given
        GameServer server = new GameServer();
        TestUser user1 = new TestUser(server);
        user1.receive().createRoom(new PlayerID(1), 3);
        TestUser user2 = new TestUser(server);
        user2.receive().joinRoom(new PlayerID(0), server.getRoomList().get(0).roomID());

        user1.receive().startGame();
        user1.sent.clear();
        user2.sent.clear();

        // when
        user1.receive().startGame();

        // then
        assertThat(user1.sent).hasSize(1).first().isInstanceOf(ErrorMessage.class);
        assertThat(user2.sent).isEmpty();
    }

    @Test
    void joining_game_using_duplicate_player_id() {
        // given
        GameServer server = new GameServer();
        TestUser user1 = new TestUser(server);
        user1.receive().createRoom(new PlayerID(1), 2);
        TestUser user2 = new TestUser(server);
        user2.sent.clear();

        // when
        user2.receive().joinRoom(new PlayerID(1), server.getRoomList().get(0).roomID());

        // then
        assertThat(server.getRoomList()).hasSize(1).first()
                .matches(info -> info.roomID() != null)
                .matches(info -> info.owner().equals(user1.user.getUserID()))
                .matches(info -> !info.isRunning())
                .matches(info -> info.players().equals(new HashMap<>() {{
                    put(new PlayerID(0), null);
                    put(new PlayerID(1), user1.user.getUserID());
                }}));
        assertThat(user2.sent).hasSize(1).first().isInstanceOf(ErrorMessage.class);
    }

    @Test
    void joining_game_using_out_of_range_player_id() {
        // given
        GameServer server = new GameServer();
        TestUser user1 = new TestUser(server);
        user1.receive().createRoom(new PlayerID(1), 2);
        TestUser user2 = new TestUser(server);
        user2.sent.clear();

        // when
        user2.receive().joinRoom(new PlayerID(123456), server.getRoomList().get(0).roomID());

        // then
        assertThat(server.getRoomList()).hasSize(1).first()
                .matches(info -> info.roomID() != null)
                .matches(info -> info.owner().equals(user1.user.getUserID()))
                .matches(info -> !info.isRunning())
                .matches(info -> info.players().equals(new HashMap<>() {{
                    put(new PlayerID(0), null);
                    put(new PlayerID(1), user1.user.getUserID());
                }}));
        assertThat(user2.sent).hasSize(1).first().isInstanceOf(ErrorMessage.class);
    }

    @Test
    void joining_game_using_null_player_id() {
        // given
        GameServer server = new GameServer();
        TestUser user1 = new TestUser(server);
        user1.receive().createRoom(new PlayerID(1), 2);
        TestUser user2 = new TestUser(server);
        user2.sent.clear();

        // when
        user2.receive().joinRoom(null, server.getRoomList().get(0).roomID());

        // then
        assertThat(server.getRoomList()).hasSize(1).first()
                .matches(info -> info.roomID() != null)
                .matches(info -> info.owner().equals(user1.user.getUserID()))
                .matches(info -> !info.isRunning())
                .matches(info -> info.players().equals(new HashMap<>() {{
                    put(new PlayerID(0), null);
                    put(new PlayerID(1), user1.user.getUserID());
                }}));
        assertThat(user2.sent).hasSize(1).first().isInstanceOf(ErrorMessage.class);
    }

    @Test
    void joining_game_using_null_room_id() {
        // given
        GameServer server = new GameServer();
        TestUser user1 = new TestUser(server);
        user1.receive().createRoom(new PlayerID(1), 2);
        TestUser user2 = new TestUser(server);
        user2.sent.clear();

        // when
        user2.receive().joinRoom(new PlayerID(0), null);

        // then
        assertThat(server.getRoomList()).hasSize(1).first()
                .matches(info -> info.roomID() != null)
                .matches(info -> info.owner().equals(user1.user.getUserID()))
                .matches(info -> !info.isRunning())
                .matches(info -> info.players().equals(new HashMap<>() {{
                    put(new PlayerID(0), null);
                    put(new PlayerID(1), user1.user.getUserID());
                }}));
        assertThat(user2.sent).anyMatch(message -> message instanceof ErrorMessage);
    }

    @Test
    void joining_game_using_invalid_room_id() {
        // given
        GameServer server = new GameServer();
        TestUser user1 = new TestUser(server);
        user1.receive().createRoom(new PlayerID(1), 2);
        TestUser user2 = new TestUser(server);
        user2.sent.clear();

        // when
        user2.receive().joinRoom(new PlayerID(0), new RoomID(-789));

        // then
        assertThat(server.getRoomList()).hasSize(1).first()
                .matches(info -> info.roomID() != null)
                .matches(info -> info.owner().equals(user1.user.getUserID()))
                .matches(info -> !info.isRunning())
                .matches(info -> info.players().equals(new HashMap<>() {{
                    put(new PlayerID(0), null);
                    put(new PlayerID(1), user1.user.getUserID());
                }}));
        assertThat(user2.sent).anyMatch(message -> message instanceof ErrorMessage);
        assertThat(user2.user.getRoom()).isNull();
        assertThat(user2.user.getPlayerID()).isNull();
    }

    @Test
    void sending_events_before_start_does_not_work() {
        // given
        GameServer server = new GameServer();

        TestUser user1 = new TestUser(server);
        user1.receive().createRoom(new PlayerID(0), 2);

        TestUser user2 = new TestUser(server);
        user2.receive().joinRoom(new PlayerID(1), user1.user.getRoom().getRoomID());

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
        GameServer server = new GameServer();

        TestUser user1 = new TestUser(server);
        user1.receive().createRoom(new PlayerID(0), 2);

        TestUser user2 = new TestUser(server);
        user2.receive().joinRoom(new PlayerID(1), user1.user.getRoom().getRoomID());

        user1.receive().startGame();

        user1.sent.clear();
        user2.sent.clear();

        // when
        user1.receive().makeAction(new CompleteTurn());

        // then
        assertThat(user1.sent).isNotEmpty().allMatch(
                message -> message instanceof RegisterEventMessage
        );
        assertThat(user2.sent).isNotEmpty().allMatch(
                message -> message instanceof RegisterEventMessage
        );
    }

    @Test
    void sending_null_events_is_handled_correctly() {
        // given
        GameServer server = new GameServer();

        TestUser user1 = new TestUser(server);
        user1.receive().createRoom(new PlayerID(0), 2);

        TestUser user2 = new TestUser(server);
        user2.receive().joinRoom(new PlayerID(1), user1.user.getRoom().getRoomID());

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
        GameServer server = new GameServer();

        TestUser user1 = new TestUser(server);
        user1.receive().createRoom(new PlayerID(0), 3);

        TestUser user2 = new TestUser(server);
        user2.receive().joinRoom(new PlayerID(1), user1.user.getRoom().getRoomID());

        user1.sent.clear();
        user2.sent.clear();

        // when
        user1.receive().startGame();

        // then
        assertThat(user1.sent)
                .anyMatch(message -> message instanceof SetCurrentRoomMessage)
                .anyMatch(message -> message instanceof SetGameStateMessage);
        assertThat(user2.sent)
                .anyMatch(message -> message instanceof SetCurrentRoomMessage)
                .anyMatch(message -> message instanceof SetGameStateMessage);
    }

    @Test
    void users_receive_room_info_and_state_after_hot_joining() {
        // given
        GameServer server = new GameServer();

        TestUser user1 = new TestUser(server);
        user1.receive().createRoom(new PlayerID(0), 2);
        user1.receive().startGame();

        TestUser user2 = new TestUser(server);

        user1.sent.clear();
        user2.sent.clear();

        // when
        user2.receive().joinRoom(new PlayerID(1), user1.user.getRoom().getRoomID());

        // then
        assertThat(user2.sent)
                .anyMatch(message -> message instanceof SetCurrentRoomMessage)
                .anyMatch(message -> message instanceof SetGameStateMessage);
    }

    @Test
    void users_receive_updated_room_info_after_someone_hot_joins() {
        // given
        GameServer server = new GameServer();

        TestUser user1 = new TestUser(server);
        user1.receive().createRoom(new PlayerID(0), 2);
        user1.receive().startGame();

        TestUser user2 = new TestUser(server);

        user1.sent.clear();
        user2.sent.clear();

        // when
        user2.receive().joinRoom(new PlayerID(1), user1.user.getRoom().getRoomID());

        // then
        assertThat(user1.sent).hasSize(1);
        SetCurrentRoomMessage message = (SetCurrentRoomMessage) user1.sent.get(0);
        assertThat(message.roomInfo()).isEqualTo(user1.user.getRoom().getRoomInfo());
    }

    @Test
    void room_info_after_hot_join_is_correct() {
        // given
        GameServer server = new GameServer();

        TestUser user1 = new TestUser(server);
        user1.receive().createRoom(new PlayerID(0), 2);
        user1.receive().startGame();

        TestUser user2 = new TestUser(server);

        // when
        user2.receive().joinRoom(new PlayerID(1), user1.user.getRoom().getRoomID());

        // then
        assertThat(server.getRoomList()).hasSize(1).first()
                .matches(info -> info.roomID() != null)
                .matches(info -> info.owner().equals(user1.user.getUserID()))
                .matches(RoomInfo::isRunning)
                .matches(info -> info.players().equals(new HashMap<>() {{
                    put(new PlayerID(0), user1.user.getUserID());
                    put(new PlayerID(1), user2.user.getUserID());
                }}));
    }
}