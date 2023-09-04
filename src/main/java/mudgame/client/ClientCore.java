package mudgame.client;

import mudgame.events.Event;

public class ClientCore {
    private final ClientGameState state;

    public ClientCore(ClientGameState state) {
        this.state = state;
    }

    public void receive(Event event) {
    }

    public ClientGameState state() {
        return state;
    }
}
