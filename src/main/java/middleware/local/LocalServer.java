package middleware.local;

import core.model.PlayerID;
import middleware.clients.GameClient;
import mudgame.controls.actions.Action;
import mudgame.server.EventOccurrence;
import mudgame.server.MudServerCore;
import mudgame.server.state.ServerState;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class LocalServer {
    private final MudServerCore core;
    private final List<GameClient> clients = new ArrayList<>();
    private final Map<PlayerID, LocalGameClient> clientMap = new HashMap<>();

    public LocalServer(ServerState state) {
        for (PlayerID playerID : state.turnManager().players()) {
            LocalGameClient client = new LocalGameClient(state.toClientGameState(playerID), this);
            clients.add(client);
            clientMap.put(playerID, client);
        }

        core = new MudServerCore(state, this::sendEvent);
    }

    public int playerCount() {
        return clients.size();
    }

    public ServerState state() {
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

    public GameClient getClient(int i) {
        return clients.get(i);
    }
}
