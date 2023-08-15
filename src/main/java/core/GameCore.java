package core;

import core.entities.EntityBoard;
import core.entities.SimpleEntityBoard;
import core.events.Event;
import core.events.Event.Action;
import core.events.EventObserver;
import core.events.EventSender;
import core.id.PlayerID;
import core.rules.ActionRule;
import core.rules.PlayerOwnsMovedEntity;
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

    GameCore(int playerCount, EventSender eventSender)
    {
        playerManager = new PlayerManager(playerCount);
        entityBoard = new SimpleEntityBoard();
        fogOfWar = new FogOfWar(playerManager.getPlayerIDs());
        eventEntityBoard = new EventEntityBoard(entityBoard, fogOfWar, eventSender);

        rules = List.of(new PlayerOwnsMovedEntity(entityBoard),
                        new PlayerSeesMoveDestination(fogOfWar));

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
