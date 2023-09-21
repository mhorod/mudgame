package middleware.communication;

import middleware.utils.MockSockets;
import middleware.utils.Wait;
import org.junit.jupiter.api.Test;

import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class SocketTest {
    @Test
    void socket_device_builder_with_bad_socket() throws Throwable {
        // given
        Socket socket = MockSockets.emptySocket();
        socket.close();
        SocketDeviceBuilder builder = new SocketDeviceBuilder(socket);

        // when
        List<Object> list = new ArrayList<>();
        Optional<NetworkDevice> device = builder.build(list::add);

        // then
        assertThat(list).isEmpty();
        assertThat(device.isEmpty()).isTrue();
    }

    @Test
    void socket_device_builder_with_good_socket() throws Throwable {
        // given
        Socket socket = MockSockets.emptySocket();
        SocketDeviceBuilder builder = new SocketDeviceBuilder(socket);

        // when
        List<Object> list = new ArrayList<>();
        Optional<NetworkDevice> device = builder.build(list::add);

        // then
        assertThat(list).isEmpty();
        assertThat(device.orElseThrow().isClosed()).isFalse();
        socket.close();
    }

    @Test
    void socket_device_with_good_socket_closes_properly() throws Throwable {
        // given
        Socket socket = MockSockets.emptySocket();
        SocketDeviceBuilder builder = new SocketDeviceBuilder(socket);

        List<Object> list = new ArrayList<>();
        Optional<NetworkDevice> device = builder.build(list::add);

        // when
        socket.close();
        Wait.verify_wait();

        // then
        assertThat(list).isEmpty();
        assertThat(device.orElseThrow().isClosed()).isTrue();
    }
}
