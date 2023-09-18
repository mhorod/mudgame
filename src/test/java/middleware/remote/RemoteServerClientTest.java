package middleware.remote;

import core.model.PlayerID;
import middleware.clients.NetworkClient;
import middleware.clients.ServerClient;
import middleware.messages_to_server.MessageToServer.CreateRoomMessage;
import middleware.messages_to_server.MessageToServer.DownloadStateMessage;
import middleware.messages_to_server.MessageToServer.GetRoomListMessage;
import middleware.messages_to_server.MessageToServer.JoinRoomMessage;
import middleware.messages_to_server.MessageToServer.LeaveRoomMessage;
import middleware.messages_to_server.MessageToServer.LoadGameMessage;
import middleware.messages_to_server.MessageToServer.SetNameMessage;
import middleware.messages_to_server.MessageToServer.StartGameMessage;
import middleware.model.RoomID;
import middleware.model.RoomInfo;
import middleware.model.UserID;
import middleware.utils.TestServerClient;
import mudgame.client.ClientGameState;
import mudgame.server.state.ClassicServerStateSupplier;
import mudgame.server.state.ServerState;
import mudgame.server.state.ServerStateSupplier;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class RemoteServerClientTest {
    private final ServerStateSupplier serverStateSupplier = new ClassicServerStateSupplier();
    private static final RoomInfo ROOM_INFO = new RoomInfo(
            new RoomID(2),
            Map.of(new PlayerID(2), "2",
                   new PlayerID(4), "4"),
            "2",
            false
    );

    @Test
    void defaults() {
        // given
        TestServerClient testClient = new TestServerClient();

        // when
        ServerClient serverClient = testClient.serverClient;

        // then
        assertThat(serverClient.getGameClient().isPresent()).isFalse();
        assertThat(serverClient.getName()).isEqualTo(UserID.DEFAULT_NAME);
        assertThat(serverClient.currentRoom().isPresent()).isFalse();
        assertThat(serverClient.hasCoreChanged()).isFalse();
        assertThat(serverClient.getDownloadedState().isPresent()).isFalse();
        assertThat(testClient.sent).isEmpty();
    }

    @Test
    void set_room_info() {
        // given
        TestServerClient testClient = new TestServerClient();
        NetworkClient networkClient = testClient.networkClient;
        ServerClient serverClient = testClient.serverClient;

        // when
        testClient.receive().setCurrentRoom(ROOM_INFO);
        networkClient.processAllMessages();

        // then
        assertThat(serverClient.currentRoom().orElseThrow()).isEqualTo(ROOM_INFO);
    }

    @Test
    void set_room_list() {
        // given
        TestServerClient testClient = new TestServerClient();
        NetworkClient networkClient = testClient.networkClient;
        ServerClient serverClient = testClient.serverClient;

        // when
        testClient.receive().setRoomList(List.of(ROOM_INFO, ROOM_INFO));
        networkClient.processAllMessages();

        // then
        assertThat(serverClient.getRoomList()).containsExactly(ROOM_INFO, ROOM_INFO);
    }

    @Test
    void operation_forwarding() {
        // given
        TestServerClient testClient = new TestServerClient();
        ServerClient serverClient = testClient.serverClient;

        // when
        serverClient.leaveCurrentRoom();
        serverClient.refreshRoomList();
        serverClient.joinRoom(new RoomID(2), new PlayerID(3));
        serverClient.createRoom(new PlayerID(4), 5);
        ServerState state = serverStateSupplier.get(2);
        serverClient.createRoom(new PlayerID(7), state);
        serverClient.startGame();
        serverClient.setName("8");
        serverClient.downloadState();

        // then
        assertThat(testClient.sent).containsExactly(
                new LeaveRoomMessage(),
                new GetRoomListMessage(),
                new JoinRoomMessage(new PlayerID(3), new RoomID(2)),
                new CreateRoomMessage(new PlayerID(4), 5),
                new LoadGameMessage(new PlayerID(7), state),
                new StartGameMessage(),
                new SetNameMessage("8"),
                new DownloadStateMessage()
        );
    }

    @Test
    void operation_forwarding_does_not_work_when_client_is_inactive() {
        // given
        TestServerClient testClient = new TestServerClient();
        NetworkClient networkClient = testClient.networkClient;
        ServerClient serverClient = testClient.serverClient;

        testClient.connection.close();
        networkClient.processAllMessages();

        // when
        serverClient.leaveCurrentRoom();
        serverClient.refreshRoomList();
        serverClient.joinRoom(new RoomID(2), new PlayerID(3));
        serverClient.createRoom(new PlayerID(4), 5);
        ServerState state = serverStateSupplier.get(6);
        serverClient.createRoom(new PlayerID(7), state);
        serverClient.startGame();
        serverClient.setName("8");
        serverClient.downloadState();

        // then
        assertThat(testClient.sent).isEmpty();
    }

    @Test
    void change_name() {
        // given
        TestServerClient testClient = new TestServerClient();
        NetworkClient networkClient = testClient.networkClient;
        ServerClient serverClient = testClient.serverClient;

        // when
        testClient.receive().changeName("name");
        networkClient.processAllMessages();

        // then
        assertThat(serverClient.getName()).isEqualTo("name");
    }

    @Test
    void set_download_state() {
        // given
        TestServerClient testClient = new TestServerClient();
        NetworkClient networkClient = testClient.networkClient;
        ServerClient serverClient = testClient.serverClient;

        // when
        ServerState state = serverStateSupplier.get(2);
        testClient.receive().setDownloadedState(state);
        networkClient.processAllMessages();

        // then
        assertThat(serverClient.getDownloadedState().orElseThrow()).isEqualTo(state);
        assertThat(serverClient.getDownloadedState()).isEmpty();
    }

    @Test
    void core_changed() {
        // given
        TestServerClient testClient = new TestServerClient();
        NetworkClient networkClient = testClient.networkClient;
        ServerClient serverClient = testClient.serverClient;

        // when
        ClientGameState state = serverStateSupplier.get(2).toClientGameState(new PlayerID(0));
        testClient.receive().setGameState(state);
        networkClient.processAllMessages();

        // then
        assertThat(serverClient.hasCoreChanged()).isTrue();
        assertThat(serverClient.hasCoreChanged()).isFalse();
    }
}
