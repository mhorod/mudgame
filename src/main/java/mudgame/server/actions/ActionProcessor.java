package mudgame.server.actions;

import mudgame.controls.actions.Action;
import core.model.PlayerID;
import mudgame.server.EventOccurrenceObserver;
import mudgame.server.ServerGameState;
import mudgame.server.actions.entities.EntityActionProcessor;
import mudgame.server.internal.InteractiveState;

public final class ActionProcessor {
    private final RuleChecker ruleChecker;
    private final InteractiveState state;
    private final Sender sender;
    private final EntityActionProcessor entityActionProcessor;
    private final CompleteTurnProcessor completeTurnProcessor;

    public ActionProcessor(
            ServerGameState state,
            EventOccurrenceObserver eventOccurrenceObserver
    ) {
        ruleChecker = new RuleChecker(state.rules());
        this.state = new InteractiveState(state);
        this.sender = new Sender(eventOccurrenceObserver);
        this.entityActionProcessor = new EntityActionProcessor(this.state, this.sender);
        this.completeTurnProcessor = new CompleteTurnProcessor(this.state, this.sender);
    }

    public void process(Action action, PlayerID actor) {
        if (!ruleChecker.satisfiesRules(action, actor))
            return;

        entityActionProcessor.process(action);
        completeTurnProcessor.process(action);
    }

}
