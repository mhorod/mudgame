package mudgame.server.actions;

import core.event.Action;
import core.event.Event;
import core.event.EventOccurrence;
import core.model.PlayerID;
import core.turns.CompleteTurn;
import mudgame.events.EventOccurrenceObserver;
import mudgame.server.ServerGameState;
import mudgame.server.actions.entities.EntityActionProcessor;
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
        this.entityActionProcessor = new EntityActionProcessor(state,
                                                               new Sender(eventOccurrenceObserver));
    }

    public void process(Action action, PlayerID actor) {
        if (!ruleChecker.satisfiesRules(action, actor))
            return;

        entityActionProcessor.process(action);
        if (action instanceof CompleteTurn a)
            completeTurn(a);
    }

    private void completeTurn(CompleteTurn action) {
        state.playerManager().completeTurn();
        eventOccurrenceObserver.receive(seenByEveryone(action));
    }

    private EventOccurrence seenByEveryone(Event event) {
        return new EventOccurrence(event, state.playerManager().getPlayerIDs());
    }
}
