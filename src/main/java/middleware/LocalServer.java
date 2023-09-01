package middleware;

import lombok.experimental.UtilityClass;
import middleware.communicators.LocalSender;
import middleware.communicators.NotifyingMessageProcessor;
import middleware.communicators.ProcessingMessageQueue;
import middleware.communicators.Sender;
import middleware.messages_to_client.MessageToClient;
import middleware.messages_to_server.MessageToServer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.IntStream;

@UtilityClass
public final class LocalServer {
    public static List<Client> of(int playerCount) {
        if (playerCount <= 0)
            throw new IllegalArgumentException("Player count should be positive");

        GameServer server = new GameServer();
        List<Client> clients = new ArrayList<>();

        for (int i = 0; i < playerCount; ++i) {
            UserID userID = new UserID(i);

            ProcessingMessageQueue<MessageToClient> clientQueue = new ProcessingMessageQueue<>();
            Sender<MessageToServer> clientSender = new LocalSender<>(new NotifyingMessageProcessor<>(userID, server));
            Sender<MessageToClient> serverSender = new LocalSender<>(clientQueue);

            server.addConnection(userID, serverSender);
            clients.add(new Client(clientSender, clientQueue));
        }

        server.startGame(IntStream.range(0, playerCount).mapToObj(UserID::new).toList());

        return Collections.unmodifiableList(clients);
    }
}
