package core.server.rules;

import core.events.Action;
import core.model.PlayerID;
import core.turns.TurnView;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public final class PlayerTakesActionDuringOwnTurn implements ActionRule {
    private final TurnView turnView;

    @Override
    public boolean isSatisfied(Action action, PlayerID actor) {
        return turnView.getCurrentPlayer().equals(actor);
    }
}
