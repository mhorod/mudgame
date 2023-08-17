package core;

import core.entities.EntityBoard;
import core.entities.SimpleEntityBoard;
import core.events.Event;
import core.events.Event.Action;
import core.events.EventObserver;
import core.events.ObserverEventSender;
import core.fogofwar.FogOfWar;
import core.model.PlayerID;
import core.rules.ActionRule;
import core.rules.CreationPositionIsEmpty;
import core.rules.MoveDestinationIsEmpty;
import core.rules.PlayerOwnsMovedEntity;
import core.rules.PlayerSeesCreationPosition;
import core.rules.PlayerSeesMoveDestination;

import java.util.List;

public class GameCore implements ActionProcessor, EventObserver
{
    public final PlayerManager playerManager;
    public final EntityBoard entityBoard;
    public final FogOfWar fogOfWar;
    public final EventEntityBoard eventEntityBoard;
    public final List<ActionRule> rules;
    private final RuleBasedActionProcessor actionProcessor;

    GameCore(int playerCount, ObserverEventSender eventSender)
    {
        playerManager = new PlayerManager(playerCount);
        entityBoard = new SimpleEntityBoard();
        fogOfWar = new FogOfWar(playerManager.getPlayerIDs());
        eventEntityBoard = new EventEntityBoard(entityBoard, fogOfWar, eventSender);

        rules = List.of(new PlayerOwnsMovedEntity(entityBoard),
                        new PlayerSeesMoveDestination(fogOfWar),
                        new PlayerSeesCreationPosition(fogOfWar),
                        new CreationPositionIsEmpty(entityBoard),
                        new MoveDestinationIsEmpty(entityBoard));

        actionProcessor = new RuleBasedActionProcessor(rules);
        actionProcessor.addObserver(eventEntityBoard);
    }

    @Override
    public void process(Action action, PlayerID actor)
    {
        actionProcessor.process(action, actor);
    }

    @Override
    public void receive(Event event)
    {
        eventEntityBoard.receive(event);
    }
}
