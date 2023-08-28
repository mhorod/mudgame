package core.client;

import core.components.ConditionalEventObserver;
import core.components.EventEntityBoard;
import core.components.EventPlayerManager;
import core.events.Event;
import core.events.EventObserver;
import core.model.PlayerID;

import java.util.function.Predicate;

public final class ClientCore implements EventObserver {

    private static final class ConditionalEventSink
            implements EventObserver, ConditionalEventObserver {
        @Override
        public void receive(
                Event event, Predicate<PlayerID> shouldPlayerReceive
        ) {
            // Sink does not process events in any way
        }

        @Override
        public void receive(Event event) {
            // Sink does not process events in any way
        }
    }

    private final ClientGameState state;

    // event processing
    private final EventPlayerManager eventPlayerManager;
    private final EventEntityBoard eventEntityBoard;

    public ClientCore(ClientGameState state) {
        this.state = state;
        ConditionalEventSink eventSink = new ConditionalEventSink();
        eventPlayerManager = new EventPlayerManager(
                state.playerManager(),
                eventSink
        );
        eventEntityBoard = new EventEntityBoard(
                state.entityBoard(),
                new ClientVisibilityPredicates(state.playerID()),
                eventSink
        );
    }

    public ClientGameState state() { return state; }

    @Override
    public void receive(Event event) {
        eventPlayerManager.receive(event);
        eventEntityBoard.receive(event);

    }
}
