package mudgame.client;

import core.event.Event;
import core.pathfinder.Pathfinder;
import mudgame.client.events.EventProcessor;

public class MudClientCore {
    private final ClientGameState state;
    private final EventProcessor eventProcessor;
    private final Pathfinder pathfinder;

    public MudClientCore(ClientGameState state) {
        this.state = state;
        this.pathfinder = new Pathfinder(state.terrain(), state.entityBoard());
        eventProcessor = new EventProcessor(state);
    }

    public void receive(Event event) {
        eventProcessor.process(event);
    }

    public ClientGameState state() {
        return state;
    }

    public Pathfinder pathfinder() {
        return pathfinder;
    }
}
