package core.server;

import core.entities.EntityBoard;
import core.entities.EntityBoardView;
import core.entities.EventEntityBoard;
import core.events.ConditionalEventObserver;
import core.events.Event;
import core.events.Action;
import core.events.EventObserver;
import core.events.EventOccurrence;
import core.fogofwar.EventFogOfWar;
import core.fogofwar.FogOfWar;
import core.model.PlayerID;
import core.server.rules.ActionRule;
import core.server.rules.CreationPositionIsEmpty;
import core.server.rules.MoveDestinationIsEmpty;
import core.server.rules.PlayerOwnsCreatedEntity;
import core.server.rules.PlayerOwnsMovedEntity;
import core.server.rules.PlayerSeesCreationPosition;
import core.server.rules.PlayerSeesMoveDestination;
import core.server.rules.PlayerTakesActionDuringOwnTurn;
import core.terrain.TerrainGenerator;
import core.terrain.TerrainGenerator.GeneratedTerrain;
import core.terrain.generators.SimpleLandGenerator;
import core.turns.EventPlayerManager;
import core.turns.PlayerManager;
import core.turns.TurnView;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.function.Predicate;

public final class ServerCore {


    @RequiredArgsConstructor
    private final class InternalSender
            implements EventObserver, ConditionalEventObserver {
        private final List<EventObserver> eventObservers;

        @Override
        public void receive(Event event, Predicate<PlayerID> shouldPlayerReceive) {
            List<PlayerID> recipients = ServerCore.this
                    .state().playerManager().getPlayerIDs()
                    .stream()
                    .filter(shouldPlayerReceive)
                    .toList();
            receive(event, recipients);
        }

        private void receive(Event event, List<PlayerID> recipients) {
            for (EventObserver observer : eventObservers)
                observer.receive(event);
            EventOccurrence occurrence = new EventOccurrence(event, recipients);
            (ServerCore.this).eventOccurrenceObserver.receive(occurrence);
        }

        @Override
        public void receive(Event event) {
            List<PlayerID> recipients = ServerCore.this.state().playerManager().getPlayerIDs();
            receive(event, recipients);
        }
    }

    private final ServerGameState state;

    private final EventOccurrenceObserver eventOccurrenceObserver;

    // rule processing
    private final RuleBasedActionProcessor actionProcessor;

    public ServerCore(ServerGameState state, EventOccurrenceObserver eventOccurrenceObserver) {
        this.eventOccurrenceObserver = eventOccurrenceObserver;
        this.state = state;

        // event processing
        EventFogOfWar eventFogOfWar = new EventFogOfWar(state.fogOfWar(), senderTo());

        EventPlayerManager eventPlayerManager = new EventPlayerManager(
                state.playerManager(),
                senderTo()
        );

        EventEntityBoard eventEntityBoard = new EventEntityBoard(
                state.entityBoard(),
                new ServerVisibilityPredicates(state.fogOfWar()),
                senderTo(eventFogOfWar)
        );


        actionProcessor = new RuleBasedActionProcessor(state.rules());
        actionProcessor.addObservers(
                eventPlayerManager,
                eventEntityBoard
        );
    }

    private InternalSender senderTo(EventObserver... observers) {
        return new InternalSender(List.of(observers));
    }

    public void process(Action action, PlayerID actor) {
        actionProcessor.process(action, actor);
    }

    public ServerGameState state() {
        return state;
    }

    static List<ActionRule> defaultRules(
            TurnView turnView,
            EntityBoardView entityBoard,
            FogOfWar fow
    ) {
        return List.of(
                new PlayerTakesActionDuringOwnTurn(turnView),

                // entity rules
                new PlayerOwnsMovedEntity(entityBoard),
                new PlayerSeesMoveDestination(fow),
                new PlayerSeesCreationPosition(fow),
                new CreationPositionIsEmpty(entityBoard),
                new PlayerOwnsCreatedEntity(),
                new MoveDestinationIsEmpty(entityBoard)
        );
    }

    public static ServerGameState newGameState(int playerCount) {
        return newGameState(playerCount, defaultTerrainGenerator());
    }

    private static TerrainGenerator defaultTerrainGenerator() {
        return new SimpleLandGenerator(2, 3, 50);
    }

    public static ServerGameState newGameState(int playerCount, TerrainGenerator terrainGenerator) {
        PlayerManager playerManager = new PlayerManager(playerCount);
        FogOfWar fow = new FogOfWar(playerManager.getPlayerIDs());
        EntityBoard entityBoard = new EntityBoard();
        GeneratedTerrain generatedTerrain = terrainGenerator.generateTerrain(playerCount);
        return new ServerGameState(
                playerManager,
                entityBoard,
                fow,
                generatedTerrain.terrain(),
                defaultRules(playerManager, entityBoard, fow)
        );
    }
}
