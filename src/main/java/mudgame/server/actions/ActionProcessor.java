package mudgame.server.actions;

import core.event.Action;
import core.event.Event;
import core.event.EventOccurrence;
import core.model.PlayerID;
import mudgame.controls.actions.CompleteTurn;
import mudgame.controls.events.SetTurn;
import mudgame.events.EventOccurrenceObserver;
import mudgame.server.ServerGameState;
import mudgame.server.actions.entities.EntityActionProcessor;
import mudgame.server.internal.InteractiveState;

public final class ActionProcessor {
    private final RuleChecker ruleChecker;
    private final InteractiveState state;
    private final Sender sender;
    private final EntityActionProcessor entityActionProcessor;

    public ActionProcessor(
            ServerGameState state,
            EventOccurrenceObserver eventOccurrenceObserver
    ) {
        ruleChecker = new RuleChecker(state.rules());
        this.state = new InteractiveState(state);
        this.sender = new Sender(eventOccurrenceObserver);
        this.entityActionProcessor = new EntityActionProcessor(this.state, this.sender);
    }

    public void process(Action action, PlayerID actor) {
        if (!ruleChecker.satisfiesRules(action, actor))
            return;

        entityActionProcessor.process(action);
        if (action instanceof CompleteTurn)
            completeTurn();
    }

    private void completeTurn() {
        state.completeTurn();
        SetTurn event = new SetTurn(state.currentPlayer());
        sender.send(seenByEveryone(event));
    }

    private EventOccurrence seenByEveryone(Event event) {
        return new EventOccurrence(event, state.players());
    }
}
