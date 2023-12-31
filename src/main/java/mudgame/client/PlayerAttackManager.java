package mudgame.client;

import core.entities.EntityBoardView;
import core.entities.model.Entity;
import core.entities.model.components.Attack;
import core.model.EntityID;
import core.model.PlayerID;
import core.model.Position;
import core.turns.TurnView;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class PlayerAttackManager {
    private final PlayerID playerID;
    private final EntityBoardView entityBoard;
    private final TurnView turnView;

    public List<EntityID> attackableEntities(EntityID attackerID) {
        Entity attacker = entityBoard.findEntityByID(attackerID);
        if (!attacker.owner().equals(playerID))
            return List.of();
        else if (attacker.getAttack().isEmpty())
            return List.of();
        else if (!turnView.currentPlayer().equals(playerID))
            return List.of();
        else if (attacker.getAttack().map(Attack::attacksLeft).orElse(0) == 0)
            return List.of();

        int range = attacker.getAttack().map(Attack::range).orElse(0);
        return entityBoard.allEntities().stream()
                .filter(e -> !e.owner().equals(playerID))
                .map(Entity::id)
                .filter(this::isEnemy)
                .filter(id -> isInRange(range, attackerID, id))
                .toList();
    }

    private boolean isEnemy(EntityID attackedID) {
        return !entityBoard.entityOwner(attackedID).equals(playerID);
    }

    private boolean isInRange(int range, EntityID attackerID, EntityID attackedID) {

        Position attackerPosition = entityBoard.entityPosition(attackerID);
        Position attackedPosition = entityBoard.entityPosition(attackedID);

        int attackDistanceSquare = attackerPosition.attackDistanceSquare(attackedPosition);
        return range * range >= attackDistanceSquare;
    }
}
