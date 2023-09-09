package mudgame.server.rules;

import core.entities.EntityBoardView;
import core.event.Action;
import core.model.PlayerID;
import lombok.RequiredArgsConstructor;
import mudgame.controls.actions.AttackEntityAction;

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
