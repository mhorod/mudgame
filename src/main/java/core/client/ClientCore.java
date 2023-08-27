package core.client;

import core.EventEntityBoard;
import core.EventPlayerManager;
import core.events.model.Event;
import core.events.observers.ConditionalEventObserver;
import core.events.observers.EventObserver;
import core.model.PlayerID;

import java.util.function.Predicate;

public final class ClientCore implements EventObserver {

    private static final class ConditionalEventSink implements ConditionalEventObserver {
        @Override
        public void receive(
                Event event, Predicate<PlayerID> shouldPlayerReceive
        ) {
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
