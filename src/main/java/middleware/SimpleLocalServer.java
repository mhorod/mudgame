package middleware;

import middleware.communicators.CommunicatorComposer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SimpleLocalServer extends SimpleServer {
    public final List<Client> clients;

    public SimpleLocalServer(int playerCount) {
        if (playerCount <= 0)
            throw new IllegalArgumentException("Player count should be positive");

        List<Client> clientsModifiableList = new ArrayList<>();

        for (int i = 0; i < playerCount; ++i) {
            UserID userID = new UserID(i);

            var communicators = CommunicatorComposer.local(this, userID);

            communicatorMap.put(userID, communicators.forServer());
            clientsModifiableList.add(new Client(communicators.forClient()));
        }

        clients = Collections.unmodifiableList(clientsModifiableList);

        startGame(communicatorMap.keySet().stream().toList());
    }
}
