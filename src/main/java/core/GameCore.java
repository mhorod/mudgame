package core;

import core.entities.EntityBoard;
import core.entities.SimpleEntityBoard;
import core.events.Event;
import core.events.Event.Action;
import core.events.EventObserver;
import core.events.EventSender;
import core.fogofwar.FogOfWar;
import core.model.PlayerID;
import core.rules.ActionRule;
import core.rules.CreationPositionIsEmpty;
import core.rules.MoveDestinationIsEmpty;
import core.rules.PlayerOwnsCreatedEntity;
import core.rules.PlayerOwnsMovedEntity;
import core.rules.PlayerSeesCreationPosition;
import core.rules.PlayerSeesMoveDestination;
import core.rules.PlayerTakesActionDuringOwnTurn;
import core.terrain.Terrain;
import core.terrain.generators.SimpleLandGenerator;

import java.util.List;

public class GameCore implements ActionProcessor, EventObserver {
    public final PlayerManager playerManager;
    private final EventPlayerManager eventPlayerManager;

    public final EntityBoard entityBoard;
    public final FogOfWar fogOfWar;
    private final EventEntityBoard eventEntityBoard;

    public final List<ActionRule> rules;
    private final RuleBasedActionProcessor actionProcessor;

    public final Terrain terrain;

    GameCore(int playerCount, EventSender eventSender) {
        playerManager = new PlayerManager(playerCount);
        eventPlayerManager = new EventPlayerManager(playerManager, eventSender);

        entityBoard = new SimpleEntityBoard();
        fogOfWar = new FogOfWar(playerManager.getPlayerIDs());
        eventEntityBoard = new EventEntityBoard(entityBoard, fogOfWar, eventSender);

        rules = List.of(new PlayerTakesActionDuringOwnTurn(playerManager),

                        // entity rules
                        new PlayerOwnsMovedEntity(entityBoard),
                        new PlayerSeesMoveDestination(fogOfWar),
                        new PlayerSeesCreationPosition(fogOfWar),
                        new CreationPositionIsEmpty(entityBoard), new PlayerOwnsCreatedEntity(),
                        new MoveDestinationIsEmpty(entityBoard));

        actionProcessor = new RuleBasedActionProcessor(rules);
        actionProcessor.addObserver(eventPlayerManager);
        actionProcessor.addObserver(eventEntityBoard);

        terrain = new SimpleLandGenerator(2, 3, 50).generateTerrain(playerCount).terrain();
    }

    @Override
    public void process(Action action, PlayerID actor) {
        actionProcessor.process(action, actor);
    }

    @Override
    public void receive(Event event) {
        eventPlayerManager.receive(event);
        eventEntityBoard.receive(event);
    }
}
