package mudgame.server;

import core.entities.EntityBoard;
import core.entities.EntityBoardView;
import core.entities.model.Entity;
import mudgame.events.ConditionalEventObserver;
import mudgame.events.Event;
import mudgame.events.Event.Action;
import mudgame.events.EventObserver;
import mudgame.events.EventOccurrence;
import mudgame.events.EventOccurrenceObserver;
import core.fogofwar.FogOfWar;
import core.model.PlayerID;
import core.model.Position;
import core.pathfinder.Pathfinder;
import mudgame.server.actions.ActionProcessor;
import mudgame.server.rules.ActionRule;
import mudgame.server.rules.CreationPositionIsEmpty;
import mudgame.server.rules.MoveDestinationIsEmpty;
import mudgame.server.rules.PlayerOwnsCreatedEntity;
import mudgame.server.rules.PlayerOwnsMovedEntity;
import mudgame.server.rules.PlayerSeesCreationPosition;
import mudgame.server.rules.PlayerSeesMoveDestination;
import mudgame.server.rules.PlayerTakesActionDuringOwnTurn;
import core.terrain.Terrain;
import core.terrain.TerrainGenerator;
import core.terrain.TerrainGenerator.GeneratedTerrain;
import core.terrain.generators.SimpleLandGenerator;
import core.turns.PlayerManager;
import core.turns.TurnView;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

import static core.entities.model.EntityType.BASE;

@Slf4j
public final class ServerCore {

    private final class InternalSender
            implements EventObserver, ConditionalEventObserver, EventOccurrenceObserver {

        private final List<EventObserver> eventObservers;
        private final List<EventOccurrenceObserver> eventOccurrenceObservers;

        public InternalSender(
                List<EventObserver> eventObservers,
                List<EventOccurrenceObserver> eventOccurrenceObservers
        ) {
            this.eventObservers = new ArrayList<>(eventObservers);
            this.eventOccurrenceObservers = new ArrayList<>(eventOccurrenceObservers);
        }

        @Override
        public void receive(Event event, Predicate<PlayerID> shouldPlayerReceive) {
            List<PlayerID> recipients = ServerCore.this
                    .players()
                    .stream()
                    .filter(shouldPlayerReceive)
                    .toList();
            receive(new EventOccurrence(event, recipients));
        }

        @Override
        public void receive(EventOccurrence occurrence) {
            log.info("Sending event occurrence: {}", occurrence);
            eventObservers.forEach(o -> o.receive(occurrence.event()));
            eventOccurrenceObservers.forEach(o -> o.receive(occurrence));
            (ServerCore.this).eventOccurrenceObserver.receive(occurrence);
        }


        @Override
        public void receive(Event event) {
            receive(new EventOccurrence(event, ServerCore.this.players()));
        }

        void addEventOccurrenceObserver(EventOccurrenceObserver o) {
            eventOccurrenceObservers.add(o);
        }
    }

    private final ServerGameState state;

    private final EventOccurrenceObserver eventOccurrenceObserver;

    // action processing
    private final ActionProcessor actionProcessor;

    private final Pathfinder pathfinder;

    public ServerCore(int playerCount) {
        this(playerCount, e -> { });
    }

    public ServerCore(int playerCount, EventOccurrenceObserver eventOccurrenceObserver) {
        this(playerCount, eventOccurrenceObserver, defaultTerrainGenerator());
    }

    public ServerCore(
            int playerCount,
            EventOccurrenceObserver eventOccurrenceObserver,
            TerrainGenerator terrainGenerator
    ) {
        GeneratedTerrain generatedTerrain = terrainGenerator.generateTerrain(playerCount);
        this.state = newState(playerCount, generatedTerrain.terrain());
        placePlayerBases(generatedTerrain.startingLocations());
        this.actionProcessor = new ActionProcessor(state.rules(), state, eventOccurrenceObserver);
        this.eventOccurrenceObserver = eventOccurrenceObserver;
        this.pathfinder = new Pathfinder(
                state.terrain(),
                state.entityBoard()
        );
    }

    public List<PlayerID> players() {
        return state.playerManager().getPlayerIDs();
    }

    public ServerCore(ServerGameState state, EventOccurrenceObserver eventOccurrenceObserver) {
        this.eventOccurrenceObserver = eventOccurrenceObserver;
        this.state = state;
        this.actionProcessor = new ActionProcessor(state.rules(), state, eventOccurrenceObserver);
        this.pathfinder = new Pathfinder(
                state.terrain(),
                state.entityBoard()
        );
    }

    private static ServerGameState newState(int playerCount, Terrain terrain) {
        PlayerManager playerManager = new PlayerManager(playerCount);
        FogOfWar fow = new FogOfWar(playerManager.getPlayerIDs());
        EntityBoard entityBoard = new EntityBoard();

        return new ServerGameState(
                playerManager,
                entityBoard,
                fow,
                terrain,
                defaultRules(playerManager, entityBoard, fow)
        );
    }

    private InternalSender sender() {
        return new InternalSender(List.of(), List.of());
    }

    private InternalSender sender(
            List<EventObserver> observers,
            List<EventOccurrenceObserver> eventOccurrenceObservers
    ) {
        return new InternalSender(observers, eventOccurrenceObservers);
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

    private static TerrainGenerator defaultTerrainGenerator() {
        return new SimpleLandGenerator(2, 4, 100);
    }


    private void placePlayerBases(List<Position> startingLocations) {
        for (int i = 0; i < startingLocations.size(); i++)
            placeBase(i, startingLocations.get(i));
    }

    private void placeBase(int i, Position position) {
        PlayerID owner = state.playerManager().getPlayerIDs().get(i);
        Entity entity = state.entityBoard().createEntity(BASE, owner, position);
        state.fogOfWar().playerFogOfWar(owner).placeEntity(entity, position);
    }
}
