package core.server;

import core.entities.EntityBoard;
import core.entities.EntityBoardView;
import core.entities.EventEntityBoard;
import core.entities.model.Entities;
import core.entities.model.Entity;
import core.events.ConditionalEventObserver;
import core.events.Event;
import core.events.Event.Action;
import core.events.EventObserver;
import core.events.EventOccurrence;
import core.fogofwar.EventFogOfWar;
import core.fogofwar.FogOfWar;
import core.model.PlayerID;
import core.model.Position;
import core.server.rules.ActionRule;
import core.server.rules.CreationPositionIsEmpty;
import core.server.rules.MoveDestinationIsEmpty;
import core.server.rules.PlayerOwnsCreatedEntity;
import core.server.rules.PlayerOwnsMovedEntity;
import core.server.rules.PlayerSeesCreationPosition;
import core.server.rules.PlayerSeesMoveDestination;
import core.server.rules.PlayerTakesActionDuringOwnTurn;
import core.terrain.EventTerrain;
import core.terrain.TerrainGenerator;
import core.terrain.TerrainGenerator.GeneratedTerrain;
import core.terrain.generators.SimpleLandGenerator;
import core.turns.EventPlayerManager;
import core.turns.PlayerManager;
import core.turns.TurnView;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.function.Predicate;

@Slf4j
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
        EventTerrain eventTerrain = new EventTerrain(state.terrain(), senderTo());
        EventFogOfWar eventFogOfWar = new EventFogOfWar(
                state.fogOfWar(),
                senderTo(eventTerrain)
        );

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
        log.info("Processing action {} by actor {}", action, actor);
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
        return new SimpleLandGenerator(2, 4, 72);
    }

    public static ServerGameState newGameState(int playerCount, TerrainGenerator terrainGenerator) {
        PlayerManager playerManager = new PlayerManager(playerCount);
        FogOfWar fow = new FogOfWar(playerManager.getPlayerIDs());
        EntityBoard entityBoard = new EntityBoard();
        GeneratedTerrain generatedTerrain = terrainGenerator.generateTerrain(playerCount);


        ServerGameState state = new ServerGameState(
                playerManager,
                entityBoard,
                fow,
                generatedTerrain.terrain(),
                defaultRules(playerManager, entityBoard, fow)
        );
        placePlayers(state, generatedTerrain.startingLocations());
        return state;
    }

    private static void placePlayers(ServerGameState state, List<Position> positions) {

        for (int i = 0; i < positions.size(); i++) {
            Position position = positions.get(i);
            PlayerID player = state.playerManager().getPlayerIDs().get(i);
            Entity entity = state.entityBoard().createEntity(
                    Entities.playerBase(),
                    player,
                    position
            );
            state.fogOfWar().playerFogOfWar(player).placeEntity(entity, position);
        }
    }

}
