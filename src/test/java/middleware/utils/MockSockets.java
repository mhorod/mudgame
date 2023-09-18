package middleware.utils;

import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;

@UtilityClass
public class MockSockets {
    @SneakyThrows
    public static Socket empty_socket() {
        Socket socket = mock(Socket.class);
        AtomicBoolean closed = new AtomicBoolean();

        doAnswer(invocation -> {
            closed.set(true);
            return null;
        }).when(socket).close();

        doAnswer(invocation ->
                         closed.get()
        ).when(socket).isClosed();

        doAnswer(invocation ->
                         new InetSocketAddress("localhost", 0).getAddress()
        ).when(socket).getInetAddress();

        doAnswer(invocation -> new OutputStream() {
            @Override
            public void write(int b) throws IOException {
                if (closed.get())
                    throw new IOException();
            }
        }).when(socket).getOutputStream();

        doAnswer(invocation -> new InputStream() {
            @Override
            public int read() throws IOException {
                while (!closed.get()) {
                }
                throw new IOException();
            }
        }).when(socket).getInputStream();

        return socket;
    }

    @SneakyThrows
    public static ServerSocketWithController serverSocket() {
        BlockingQueue<Socket> queue = new LinkedBlockingQueue<>();
        ServerSocket socket = mock(ServerSocket.class);
        AtomicBoolean closed = new AtomicBoolean();

        doAnswer(invocation -> {
            closed.set(true);
            return null;
        }).when(socket).close();

        doAnswer(invocation ->
                         closed.get()
        ).when(socket).isClosed();

        doAnswer(invocation -> {
            while (!closed.get()) {
                if (!queue.isEmpty())
                    return queue.poll();
            }
            throw new IOException();
        }).when(socket).accept();

        return new ServerSocketWithController(socket, queue::add);
    }

    public record ServerSocketWithController(ServerSocket socket, Consumer<Socket> controller) {
    }
}
