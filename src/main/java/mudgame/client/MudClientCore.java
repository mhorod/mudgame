package mudgame.client;

import core.client.ClientCore;
import core.event.Event;
import core.pathfinder.Pathfinder;

public class MudClientCore implements ClientCore {
    private final ClientGameState state;
    private final Pathfinder pathfinder;

    public MudClientCore(ClientGameState state) {
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
