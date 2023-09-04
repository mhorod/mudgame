package middleware.server;

import middleware.communication.Sender;
import middleware.communication.SocketReceiver;
import middleware.communication.SocketSender;
import middleware.messages_to_client.MessageToClient;
import middleware.messages_to_client.PingToClient;
import middleware.messages_to_server.MessageToServer;
import middleware.model.UserID;

import java.net.Socket;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public final class ServerConnector {
    private final UserID userID;
    private final GameServer server;
    private final Sender<MessageToClient> sender;

    private final AtomicInteger sinceLastTick = new AtomicInteger();
    private final AtomicBoolean last0 = new AtomicBoolean();
    private final AtomicBoolean isClosed = new AtomicBoolean();

    private ServerConnector(UserID userID, GameServer server, Sender<MessageToClient> sender) {
        this.userID = userID;
        this.server = server;
        this.sender = sender;
        server.addConnection(userID, sender);
    }

    public ServerConnector(UserID userID, GameServer server, Socket socket) {
        this(userID, server, new SocketSender<>(socket));
        new SocketReceiver<>(socket, MessageToServer.class, this::registerMessage);
    }

    private void registerMessage(MessageToServer message) {
        sinceLastTick.incrementAndGet();
        server.processMessage(userID, message);
    }

    private void checkSenderClosed() {
        if (sender.isClosed())
            close();
    }

    public void tick() {
        checkSenderClosed();
        if (isClosed.get())
            return;

        int sinceLast = sinceLastTick.getAndSet(0);
        if (sinceLast > 0) {
            last0.set(false);
            return;
        }
        if (last0.get())
            close();
        else {
            sender.sendMessage(new PingToClient("pingFromServer", true));
            last0.set(true);
        }
    }

    public void close() {
        if (isClosed.get())
            return;
        sender.close();
        isClosed.set(true);
        server.removeConnection(userID);
    }

    public boolean isClosed() {
        checkSenderClosed();
        return isClosed.get();
    }
}
