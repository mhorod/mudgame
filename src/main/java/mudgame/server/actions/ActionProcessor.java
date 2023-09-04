package mudgame.server.actions;

import core.model.PlayerID;
import core.turns.CompleteTurn;
import mudgame.controls.events.CreateEntity;
import mudgame.controls.events.MoveEntity;
import mudgame.events.Action;
import mudgame.events.Event;
import mudgame.events.EventOccurrence;
import mudgame.events.EventOccurrenceObserver;
import mudgame.server.ServerGameState;
import mudgame.server.rules.ActionRule;

import java.util.List;

public final class ActionProcessor {
    private final RuleChecker ruleChecker;
    private final ServerGameState state;
    private final EventOccurrenceObserver eventOccurrenceObserver;
    private final EntityActionProcessor entityActionProcessor;

    public ActionProcessor(
            List<ActionRule> rules, ServerGameState state,
            EventOccurrenceObserver eventOccurrenceObserver
    ) {
        ruleChecker = new RuleChecker(rules);
        this.state = state;
        this.eventOccurrenceObserver = eventOccurrenceObserver;
        this.entityActionProcessor = new EntityActionProcessor(state, eventOccurrenceObserver);
    }

    public void process(Action action, PlayerID actor) {
        if (!ruleChecker.satisfiesRules(action, actor))
            return;

        if (action instanceof CompleteTurn a)
            completeTurn(a);
        else if (action instanceof CreateEntity a)
            entityActionProcessor.createEntity(a);
        else if (action instanceof MoveEntity a)
            entityActionProcessor.moveEntity(a);
    }

    private void completeTurn(CompleteTurn action) {
        state.playerManager().completeTurn();
        eventOccurrenceObserver.receive(seenByEveryone(action));
    }

    private EventOccurrence seenByEveryone(Event event) {
        return new EventOccurrence(event, state.playerManager().getPlayerIDs());
    }
}
