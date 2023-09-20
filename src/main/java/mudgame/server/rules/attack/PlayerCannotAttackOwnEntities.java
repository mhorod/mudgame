package mudgame.server.rules.attack;

import core.entities.EntityBoardView;
import mudgame.controls.actions.Action;
import core.model.PlayerID;
import lombok.RequiredArgsConstructor;
import mudgame.controls.actions.AttackEntityAction;
import mudgame.server.rules.ActionRule;

@RequiredArgsConstructor
public class PlayerCannotAttackOwnEntities implements ActionRule {
    private final EntityBoardView entityBoard;

    @Override
    public boolean isSatisfied(Action action, PlayerID actor) {
        if (action instanceof AttackEntityAction a)
            return !actor.equals(entityBoard.entityOwner(a.attacked()));
        else
            return true;
    }
}
