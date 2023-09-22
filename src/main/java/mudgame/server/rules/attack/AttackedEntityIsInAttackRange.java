package mudgame.server.rules.attack;

import core.entities.EntityBoardView;
import core.entities.model.Entity;
import core.entities.model.components.Attack;
import core.model.EntityID;
import core.model.PlayerID;
import core.model.Position;
import lombok.RequiredArgsConstructor;
import mudgame.controls.actions.Action;
import mudgame.controls.actions.AttackEntityAction;
import mudgame.server.rules.ActionRule;

@RequiredArgsConstructor
public class AttackedEntityIsInAttackRange implements ActionRule {
    private final EntityBoardView entityBoard;

    @Override
    public boolean isSatisfied(Action action, PlayerID actor) {
        if (action instanceof AttackEntityAction a)
            return attackedEntityIsInRange(a.attacker(), a.attacked());
        else
            return true;
    }

    private boolean attackedEntityIsInRange(EntityID attackerID, EntityID attackedID) {
        if (!entityBoard.containsEntity(attackerID) || !entityBoard.containsEntity(attackedID))
            return false;
        Entity attacker = entityBoard.findEntityByID(attackerID);
        if (attacker == null || attacker.getAttack().isEmpty())
            return false;
        int range = attacker.getAttack().map(Attack::range).orElse(0);

        Position attackerPosition = entityBoard.entityPosition(attackerID);
        Position attackedPosition = entityBoard.entityPosition(attackedID);

        int attackDistanceSquare = attackerPosition.attackDistanceSquare(attackedPosition);
        return range * range >= attackDistanceSquare;
    }
}
