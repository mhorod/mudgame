package middleware.communicators;

import lombok.experimental.UtilityClass;
import middleware.NotificationProcessor;
import middleware.UserID;
import middleware.messages_to_client.MessageToClient;
import middleware.messages_to_server.MessageToServer;

import java.net.Socket;

@UtilityClass
public class CommunicatorComposer {
    public static CommunicatorPair local(NotificationProcessor processor, UserID userID) {
        MessageQueue<MessageToServer> serverQueue = new NotifyingMessageQueue<>(new MessageQueueImpl<>(), processor, userID);
        MessageQueue<MessageToClient> clientQueue = new MessageQueueImpl<>();

        return new CommunicatorPair(
                new ServerSideCommunicator(new LocalSender<>(clientQueue), serverQueue),
                new ClientSideCommunicator(new LocalSender<>(serverQueue), clientQueue)
        );
    }

    public static ServerSideCommunicator remote(NotificationProcessor processor, UserID userID, Socket socket) {
        MessageQueue<MessageToServer> incomingMessageQueue = new NotifyingMessageQueue<>(new MessageQueueImpl<>(), processor, userID);

        return new ServerSideCommunicator(
                new NetworkSender<>(socket, new MessageQueueImpl<>()),
                new NetworkReceiver<>(socket, incomingMessageQueue, MessageToServer.class)
        );
    }

    public static ClientSideCommunicator remote(Socket socket) {
        return new ClientSideCommunicator(
                new NetworkSender<>(socket, new MessageQueueImpl<>()),
                new NetworkReceiver<>(socket, new MessageQueueImpl<>(), MessageToClient.class)
        );
    }

    public record CommunicatorPair(ServerSideCommunicator forServer, ClientSideCommunicator forClient) {
    }
}
