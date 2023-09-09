package mudgame.server.rules;

import core.entities.EntityBoardView;
import core.entities.components.visitors.GetAttack;
import core.entities.model.Entity;
import core.event.Action;
import core.model.EntityID;
import core.model.PlayerID;
import core.model.Position;
import lombok.RequiredArgsConstructor;
import mudgame.controls.actions.AttackEntityAction;

@RequiredArgsConstructor
public class AttackedEntityIsInAttackRange implements ActionRule {
    private final EntityBoardView entityBoard;
    private final GetAttack getAttack = new GetAttack();

    @Override
    public boolean isSatisfied(Action action, PlayerID actor) {
        if (action instanceof AttackEntityAction a)
            return attackedEntityIsInRange(a.attacker(), a.attacked());
        else
            return true;
    }

    private boolean attackedEntityIsInRange(EntityID attackerID, EntityID attackedID) {
        Entity attacker = entityBoard.findEntityByID(attackerID);
        int range = getAttack.getAttack(attacker).range();

        Position attackerPosition = entityBoard.entityPosition(attackerID);
        Position attackedPosition = entityBoard.entityPosition(attackedID);

        int attackDistanceSquare = attackerPosition.attackDistanceSquare(attackedPosition);
        return range * range >= attackDistanceSquare;
    }
}
