package mudgame.server.rules;

import core.entities.EntityBoardView;
import core.entities.components.visitors.GetAttack;
import core.event.Action;
import core.model.PlayerID;
import lombok.RequiredArgsConstructor;
import mudgame.controls.actions.AttackEntityAction;

@RequiredArgsConstructor
public class AttackerEntityHasAttackComponent implements ActionRule {
    private final EntityBoardView entityBoard;
    private final GetAttack getAttack = new GetAttack();

    @Override
    public boolean isSatisfied(Action action, PlayerID actor) {
        if (action instanceof AttackEntityAction a)
            return getAttack.getAttack(entityBoard.findEntityByID(a.attacker())) != null;
        else
            return true;
    }
}
