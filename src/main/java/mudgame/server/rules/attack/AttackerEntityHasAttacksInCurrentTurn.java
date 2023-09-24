package mudgame.server.rules.attack;

import core.entities.EntityBoardView;
import core.entities.model.Entity;
import core.model.EntityID;
import core.model.PlayerID;
import lombok.RequiredArgsConstructor;
import mudgame.controls.actions.Action;
import mudgame.controls.actions.AttackEntityAction;
import mudgame.server.rules.ActionRule;

@RequiredArgsConstructor
public class AttackerEntityHasAttacksInCurrentTurn implements ActionRule {
    private final EntityBoardView entityBoard;

    @Override
    public boolean isSatisfied(Action action, PlayerID actor) {
        if (action instanceof AttackEntityAction a)
            return attackerEntityHasAttacksLeft(a.attacker());
        else
            return true;
    }

    private boolean attackerEntityHasAttacksLeft(EntityID attackerID) {
        Entity attacker = entityBoard.findEntityByID(attackerID);
        if (attacker == null)
            return false;
        else
            return attacker.getAttack().map(a -> a.attacksLeft() > 0).orElse(false);
    }
}
