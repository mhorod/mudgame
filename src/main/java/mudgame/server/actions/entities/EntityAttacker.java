package mudgame.server.actions.entities;

import core.entities.EntityBoardView;
import core.entities.components.visitors.GetAttack;
import core.entities.components.visitors.GetHealth;
import core.entities.model.Entity;
import core.fogofwar.FogOfWar;
import core.model.EntityID;
import core.model.PlayerID;
import core.model.Position;
import lombok.RequiredArgsConstructor;
import mudgame.controls.actions.AttackEntityAction;
import mudgame.controls.events.AttackEntityEvent;
import mudgame.controls.events.AttackPosition;
import mudgame.controls.events.DamageEntity;
import mudgame.controls.events.KillEntity;
import mudgame.server.actions.Sender;

@RequiredArgsConstructor
class EntityAttacker {
    private final Sender sender;
    private final EntityBoardView entityBoard;
    private final FogOfWar fow;

    private final GetAttack getAttack = new GetAttack();
    private final GetHealth getHealth = new GetHealth();

    void attackEntity(AttackEntityAction a) {
        Entity originalAttacker = entityBoard.findEntityByID(a.attacker());
        Entity originalAttacked = entityBoard.findEntityByID(a.attacked());

        attack(originalAttacker, originalAttacked);
        // The attacked entity strikes back if it can
        attack(originalAttacked, originalAttacker);
    }

    void attack(Entity attacker, Entity attacked) {
        if (getAttack.getAttack(attacker) == null || getHealth.getHealth(attacked) == null)
            return;
        if (getAttack.getAttack(attacker) == null || getHealth.getHealth(attacked) <= 0)
            return;
        int damage = getAttack.getAttack(attacker).damage();
        int healthLeft = attacked.damage(damage).orElse(0);
        sendAttack(attacker, attacked, damage);
        if (healthLeft <= 0)
            sendKill(attacked.id());
    }

    private void sendKill(EntityID entityID) {
        Position position = entityBoard.entityPosition(entityID);
        for (PlayerID player : fow.players())
            if (fow.isVisible(position, player))
                sender.send(new KillEntity(entityID), player);
    }

    private void sendAttack(Entity attacker, Entity attacked, int damage) {
        for (PlayerID player : fow.players())
            sendAttack(player, attacker, attacked, damage);
    }

    private void sendAttack(PlayerID player, Entity attacker, Entity attacked, int damage) {
        Position attackerPosition = entityBoard.entityPosition(attacker.id());
        Position attackedPosition = entityBoard.entityPosition(attacked.id());

        if (fow.isVisible(attackerPosition, player) && fow.isVisible(attackedPosition, player))
            sender.send(new AttackEntityEvent(attacker.id(), attacked.id(), damage), player);
        else if (fow.isVisible(attackerPosition, player))
            sender.send(new AttackPosition(attacker.id(), attackedPosition), player);
        else if (fow.isVisible(attackedPosition, player))
            sender.send(new DamageEntity(attacked.id(), damage), player);
    }
}
