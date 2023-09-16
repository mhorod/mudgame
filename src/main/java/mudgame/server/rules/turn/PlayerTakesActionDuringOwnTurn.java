package mudgame.server.rules.turn;

import mudgame.controls.actions.Action;
import core.model.PlayerID;
import core.turns.TurnView;
import lombok.RequiredArgsConstructor;
import mudgame.server.rules.ActionRule;

@RequiredArgsConstructor
public final class PlayerTakesActionDuringOwnTurn implements ActionRule {
    private final TurnView turnView;

    @Override
    public boolean isSatisfied(Action action, PlayerID actor) {
        return turnView.currentPlayer().equals(actor);
    }
}
