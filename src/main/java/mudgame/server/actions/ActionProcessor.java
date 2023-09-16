package mudgame.server.actions;

import core.model.PlayerID;
import mudgame.controls.actions.Action;
import mudgame.server.EventOccurrenceObserver;
import mudgame.server.ServerGameState;
import mudgame.server.actions.entities.EntityActionProcessor;
import mudgame.server.internal.InteractiveState;

public final class ActionProcessor {
    private final RuleChecker ruleChecker;
    private final EntityActionProcessor entityActionProcessor;
    private final CompleteTurnProcessor completeTurnProcessor;

    public ActionProcessor(
            ServerGameState serverState,
            EventOccurrenceObserver eventOccurrenceObserver
    ) {
        ruleChecker = new RuleChecker(serverState.rules());
        InteractiveState state = new InteractiveState(serverState);
        EventSender sender = new EventSender(eventOccurrenceObserver);
        this.entityActionProcessor = new EntityActionProcessor(state, sender);
        this.completeTurnProcessor = new CompleteTurnProcessor(state, sender);
    }

    public void process(Action action, PlayerID actor) {
        if (!ruleChecker.satisfiesRules(action, actor))
            return;

        entityActionProcessor.process(action);
        completeTurnProcessor.process(action);
    }

}
