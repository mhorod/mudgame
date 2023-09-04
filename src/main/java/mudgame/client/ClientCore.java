package mudgame.client;

import core.pathfinder.Pathfinder;
import mudgame.events.Event;

public class ClientCore {
    private final ClientGameState state;
    private final Pathfinder pathfinder;

    public ClientCore(ClientGameState state) {
        this.state = state;
        this.pathfinder = new Pathfinder(state.terrain(), state.entityBoard());
    }

    public void receive(Event event) {
    }

    public ClientGameState state() {
        return state;
    }

    public Pathfinder pathfinder() {
        return pathfinder;
    }
}
