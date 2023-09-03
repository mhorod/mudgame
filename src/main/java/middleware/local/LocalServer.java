package middleware.local;

import core.events.Action;
import core.events.EventOccurrence;
import core.model.PlayerID;
import core.server.ServerCore;
import core.server.ServerGameState;
import middleware.clients.GameClient;

import java.util.*;

public final class LocalServer {
    private final ServerCore core;
    private final List<GameClient> clients = new ArrayList<>();
    private final Map<PlayerID, LocalClient> clientMap = new HashMap<>();

    public LocalServer(ServerGameState state) {
        for (PlayerID playerID : state.playerManager().getPlayerIDs()) {
            LocalClient client = new LocalClient(state.toClientGameState(playerID), this);
            clients.add(client);
            clientMap.put(playerID, client);
        }

        core = new ServerCore(state, this::sendEvent);
    }

    // TODO this is cursed, fix it
    public LocalServer(int playerCount) {
        this(new ServerCore(playerCount).state());
    }

    public ServerGameState state() {
        return core.state();
    }

    public List<GameClient> getClients() {
        return Collections.unmodifiableList(clients);
    }

    void processAction(Action action, PlayerID actor) {
        core.process(action, actor);
    }

    private void sendEvent(EventOccurrence eventOccurrence) {
        for (PlayerID playerID : eventOccurrence.recipients())
            clientMap.get(playerID).registerEvent(eventOccurrence.event());
    }
}
