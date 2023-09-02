package middleware;

import lombok.experimental.UtilityClass;
import middleware.communicators.CommunicatorComposer;

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

            var communicators = CommunicatorComposer.local(server, userID);

            server.addConnection(userID, communicators.forServer());
            clients.add(new Client(communicators.forClient()));
        }

        server.startGame(IntStream.range(0, playerCount).mapToObj(UserID::new).toList());

        return Collections.unmodifiableList(clients);
    }
}
