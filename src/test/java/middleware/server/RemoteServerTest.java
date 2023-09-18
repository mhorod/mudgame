package middleware.server;

import mudgame.server.state.ServerStateSupplier;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Timer;
import java.util.TimerTask;

import static middleware.utils.MockSockets.*;
import static middleware.utils.Wait.verify_wait;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class RemoteServerTest {
    private static final ServerStateSupplier serverStateSupplier = mock(ServerStateSupplier.class);

    @Test
    void stop_cancels_timer_and_socket() {
        // given
        Timer mockedTimer = mock(Timer.class);
        ServerSocketWithController mockedServerSocket = serverSocket();

        RemoteServer server = new RemoteServer(serverStateSupplier, mockedServerSocket.socket(),
                                               mockedTimer);

        // when
        server.stop();
        verify_wait();

        // then
        verify(mockedTimer, atLeastOnce()).cancel();
        assertThat(mockedServerSocket.socket().isClosed()).isTrue();
    }

    @Test
    void socket_failure_cancels_timer() throws Throwable {
        // given
        Timer mockedTimer = mock(Timer.class);
        ServerSocketWithController mockedServerSocket = serverSocket();

        RemoteServer server = new RemoteServer(serverStateSupplier, mockedServerSocket.socket(),
                                               mockedTimer);

        // when
        mockedServerSocket.socket().close();
        verify_wait();

        // then
        verify(mockedTimer, atLeastOnce()).cancel();
    }

    @Test
    void stop_cancels_timer_and_socket_and_closes_all_connections() throws Throwable {
        // given
        Timer mockedTimer = mock(Timer.class);
        ServerSocketWithController mockedServerSocket = serverSocket();
        Socket userSocket = empty_socket();

        RemoteServer server = new RemoteServer(serverStateSupplier, mockedServerSocket.socket(),
                                               mockedTimer);
        mockedServerSocket.controller().accept(userSocket);
        verify_wait();

        // when
        server.stop();
        verify_wait();

        // then
        verify(mockedTimer, atLeastOnce()).cancel();
        assertThat(mockedServerSocket.socket().isClosed()).isTrue();
        assertThat(userSocket.isClosed()).isTrue();
    }

    @Test
    void socket_failure_cancels_timer_and_closes_all_connections() throws Throwable {
        // given
        Timer mockedTimer = mock(Timer.class);
        ServerSocketWithController mockedServerSocket = serverSocket();
        Socket userSocket = empty_socket();

        RemoteServer server = new RemoteServer(serverStateSupplier, mockedServerSocket.socket(),
                                               mockedTimer);
        mockedServerSocket.controller().accept(userSocket);
        verify_wait();

        // when
        mockedServerSocket.socket().close();
        verify_wait();

        // then
        verify(mockedTimer, atLeastOnce()).cancel();
        assertThat(userSocket.isClosed()).isTrue();
    }

    @Test
    void removal_task_is_scheduled() throws Throwable {
        // given
        Timer mockedTimer = mock(Timer.class);
        ArgumentCaptor<TimerTask> captor = ArgumentCaptor.forClass(TimerTask.class);

        Socket userSocket = empty_socket();
        ServerSocketWithController mockedServerSocket = serverSocket();
        RemoteServer server = new RemoteServer(serverStateSupplier, mockedServerSocket.socket(),
                                               mockedTimer);
        mockedServerSocket.controller().accept(userSocket);
        verify_wait();

        // when
        verify(mockedTimer).schedule(captor.capture(), anyLong(), anyLong());
        captor.getValue().run();
        captor.getValue().run();
        verify_wait();

        // then
        assertThat(userSocket.isClosed()).isTrue();
        server.stop();
    }

    @Test
    void server_socket_throws_on_close() throws Throwable {
        // given
        Timer mockedTimer = mock(Timer.class);
        ServerSocket socket = mock(ServerSocket.class);

        doReturn(true).when(socket).isClosed();
        doThrow(new IOException()).when(socket).close();
        doThrow(new IOException()).when(socket).accept();

        RemoteServer server = new RemoteServer(serverStateSupplier, socket, mockedTimer);

        // when
        server.stop();
        verify_wait();

        // then
        verify(mockedTimer, atLeastOnce()).cancel();
        verify(socket, atLeastOnce()).close();
    }
}
