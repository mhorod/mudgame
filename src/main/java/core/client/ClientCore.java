package core.client;

import core.entities.EventEntityBoard;
import core.events.ConditionalEventObserver;
import core.events.Event;
import core.events.EventObserver;
import core.fogofwar.EventPlayerFogOfWar;
import core.model.PlayerID;
import core.turns.EventPlayerManager;

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
    private final EventPlayerFogOfWar eventPlayerFogOfWar;

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
        eventPlayerFogOfWar = new EventPlayerFogOfWar(state.fogOfWar(), eventSink);
    }

    public ClientGameState state() { return state; }

    @Override
    public void receive(Event event) {
        eventPlayerManager.receive(event);
        eventEntityBoard.receive(event);
        eventPlayerFogOfWar.receive(event);
    }
}