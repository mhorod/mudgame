package middleware.utils;

import middleware.communication.NetworkDevice;
import middleware.messages_to_client.MessageToClient.KickMessage;
import middleware.messages_to_server.MessageToServerHandler;
import middleware.server.GameServer;
import middleware.server.User;

import java.util.List;

public final class TestUser {
    private final TestConnection connection = new TestConnection();

    public final User user;
    public final NetworkDevice device = connection;
    public final List<Object> sent = connection.sent;

    public TestUser(GameServer server) {
        connection.setSendFilter(message -> !(message instanceof KickMessage));
        user = server.createUser(connection);
    }

    public TestUser(GameServer server, String name) {
        this(server);
        receive().setName(name);
    }

    public MessageToServerHandler receive() {
        return connection.receiveFromClient();
    }

    public TestUser allowKick() {
        connection.clearSendFilter();
        return this;
    }
}
