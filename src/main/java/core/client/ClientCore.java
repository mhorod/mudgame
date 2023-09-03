package core.client;

import core.entities.EventEntityBoard;
import core.events.ConditionalEventObserver;
import core.events.Event;
import core.events.EventObserver;
import core.events.EventOccurrence;
import core.events.EventOccurrenceObserver;
import core.fogofwar.EventPlayerFogOfWar;
import core.model.PlayerID;
import core.pathfinder.Pathfinder;
import core.terrain.EventTerrain;
import core.turns.EventPlayerManager;
import lombok.extern.slf4j.Slf4j;

import java.util.function.Predicate;

@Slf4j
public final class ClientCore implements EventObserver {

    private static final class ConditionalEventSink
            implements EventObserver, ConditionalEventObserver, EventOccurrenceObserver {
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

        @Override
        public void receive(EventOccurrence eventOccurrence) {
            // Sink does not process events in any way
        }
    }

    private final ClientGameState state;

    // event processing
    private final EventPlayerManager eventPlayerManager;
    private final EventEntityBoard eventEntityBoard;
    private final EventPlayerFogOfWar eventPlayerFogOfWar;
    private final EventTerrain eventTerrain;

    private final Pathfinder pathfinder;

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
        eventTerrain = new EventTerrain(state.terrain(), eventSink);
        pathfinder = new Pathfinder(
                state().terrain(),
                state.entityBoard(),
                state.entityMovementManager()
        );
    }

    public ClientGameState state() { return state; }

    public Pathfinder pathfinder() { return pathfinder; }

    @Override
    public void receive(Event event) {
        log.info("Received event {}", event);
        eventPlayerManager.receive(event);
        eventEntityBoard.receive(event);
        eventPlayerFogOfWar.receive(event);
        eventTerrain.receive(event);
    }
}
