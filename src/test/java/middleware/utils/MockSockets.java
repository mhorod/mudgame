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
public final class MockSockets {
    @SneakyThrows
    public static Socket emptySocket() {
        Socket socket = mock(Socket.class);
        AtomicBoolean closed = new AtomicBoolean();

        doAnswer(invocation -> {
            closed.set(true);
            return null;
        }).when(socket).close();

        doAnswer(invocation -> closed.get()).when(socket).isClosed();

        doAnswer(invocation -> new InetSocketAddress("localhost", 0).getAddress())
                .when(socket).getInetAddress();

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

    @SneakyThrows
    public Socket[] connectedSockets() {
        AtomicBoolean closed = new AtomicBoolean();

        StreamPair[] streams = new StreamPair[]{createStreams(closed), createStreams(closed)};
        Socket[] sockets = new Socket[2];

        for (int i = 0; i < 2; ++i) {
            sockets[i] = mock(Socket.class);

            doAnswer(invocation -> {
                closed.set(true);
                return null;
            }).when(sockets[i]).close();

            doAnswer(invocation -> closed.get()).when(sockets[i]).isClosed();

            doAnswer(invocation -> new InetSocketAddress("localhost", 0).getAddress())
                    .when(sockets[i]).getInetAddress();

            int finalI = i;
            doAnswer(invocation -> streams[finalI].output).when(sockets[i]).getOutputStream();
            doAnswer(invocation -> streams[finalI ^ 1].input).when(sockets[i]).getInputStream();
        }

        return sockets;
    }

    private StreamPair createStreams(AtomicBoolean closed) {
        BlockingQueue<Integer> queue = new LinkedBlockingQueue<>();

        OutputStream output = new OutputStream() {
            @Override
            public void write(int b) throws IOException {
                if (closed.get())
                    throw new IOException();
                queue.add(b);
            }

            @Override
            public void close() {
                closed.set(true);
            }
        };

        InputStream input = new InputStream() {
            @Override
            public int read() throws IOException {
                if (closed.get())
                    throw new IOException();
                int data;
                try {
                    data = queue.take();
                } catch (InterruptedException e) {
                    throw new IOException(e);
                }
                if (closed.get())
                    throw new IOException();
                return data;
            }

            @Override
            public void close() {
                closed.set(true);
            }
        };

        return new StreamPair(output, input);
    }

    private record StreamPair(OutputStream output, InputStream input) { }

    public record ServerSocketWithController(ServerSocket socket, Consumer<Socket> controller) { }
}
