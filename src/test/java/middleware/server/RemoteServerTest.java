package middleware.server;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.Duration;
import java.util.Timer;
import java.util.TimerTask;

import static middleware.server.MockSockets.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class RemoteServerTest {
    private static final Duration VERIFY_WAIT = Duration.ofMillis(100);

    @Test
    void stop_cancels_timer_and_socket() throws Throwable {
        // given
        Timer mockedTimer = mock(Timer.class);
        ServerSocketWithController mockedServerSocket = serverSocket();

        RemoteServer server = new RemoteServer(mockedServerSocket.socket(), mockedTimer);

        // when
        server.stop();
        Thread.sleep(VERIFY_WAIT);

        // then
        verify(mockedTimer, atLeastOnce()).cancel();
        assertThat(mockedServerSocket.socket().isClosed()).isTrue();
    }

    @Test
    void socket_failure_cancels_timer() throws Throwable {
        // given
        Timer mockedTimer = mock(Timer.class);
        ServerSocketWithController mockedServerSocket = serverSocket();

        RemoteServer server = new RemoteServer(mockedServerSocket.socket(), mockedTimer);

        // when
        mockedServerSocket.socket().close();
        Thread.sleep(VERIFY_WAIT);

        // then
        verify(mockedTimer, atLeastOnce()).cancel();
    }

    @Test
    void stop_cancels_timer_and_socket_and_closes_all_connections() throws Throwable {
        // given
        Timer mockedTimer = mock(Timer.class);
        ServerSocketWithController mockedServerSocket = serverSocket();
        Socket userSocket = socket();

        RemoteServer server = new RemoteServer(mockedServerSocket.socket(), mockedTimer);
        mockedServerSocket.controller().accept(userSocket);
        Thread.sleep(VERIFY_WAIT);

        // when
        server.stop();
        Thread.sleep(VERIFY_WAIT);

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
        Socket userSocket = socket();

        RemoteServer server = new RemoteServer(mockedServerSocket.socket(), mockedTimer);
        mockedServerSocket.controller().accept(userSocket);
        Thread.sleep(VERIFY_WAIT);

        // when
        mockedServerSocket.socket().close();
        Thread.sleep(VERIFY_WAIT);

        // then
        verify(mockedTimer, atLeastOnce()).cancel();
        assertThat(userSocket.isClosed()).isTrue();
    }

    @Test
    void removal_task_is_scheduled() throws Throwable {
        // given
        Timer mockedTimer = mock(Timer.class);
        ArgumentCaptor<TimerTask> captor = ArgumentCaptor.forClass(TimerTask.class);

        Socket userSocket = socket();
        ServerSocketWithController mockedServerSocket = serverSocket();
        RemoteServer server = new RemoteServer(mockedServerSocket.socket(), mockedTimer);
        mockedServerSocket.controller().accept(userSocket);
        Thread.sleep(VERIFY_WAIT);

        // when
        verify(mockedTimer).schedule(captor.capture(), anyLong(), anyLong());
        captor.getValue().run();
        captor.getValue().run();
        Thread.sleep(VERIFY_WAIT);

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

        RemoteServer server = new RemoteServer(socket, mockedTimer);

        // when
        server.stop();

        // then
        verify(mockedTimer, atLeastOnce()).cancel();
        verify(socket, atLeastOnce()).close();
    }
}
