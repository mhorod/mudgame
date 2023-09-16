package mudgame.server.actions.entities;

import core.entities.model.Entity;
import core.entities.model.components.visitors.GetAttack;
import core.entities.model.components.visitors.GetHealth;
import mudgame.controls.events.Event;
import core.model.PlayerID;
import core.model.Position;
import mudgame.controls.actions.AttackEntityAction;
import mudgame.controls.events.AttackEntityEvent;
import mudgame.controls.events.AttackPosition;
import mudgame.controls.events.DamageEntity;
import mudgame.controls.events.KillEntity;
import mudgame.controls.events.VisibilityChange;
import mudgame.server.actions.EventSender;
import mudgame.server.internal.InteractiveState;
import mudgame.server.internal.RemovedEntity;

class AttackProcessor {
    private final InteractiveState state;
    private final EventSender sender;

    private final GetAttack getAttack = new GetAttack();
    private final GetHealth getHealth = new GetHealth();

    AttackProcessor(InteractiveState state, EventSender sender) {
        this.state = state;
        this.sender = sender;
    }


    void attackEntity(AttackEntityAction a) {
        Entity originalAttacker = state.findEntityByID(a.attacker());
        Entity originalAttacked = state.findEntityByID(a.attacked());

        attack(originalAttacker, originalAttacked);
        // The attacked entity strikes back if it can
        attack(originalAttacked, originalAttacker);
    }

    void attack(Entity attacker, Entity attacked) {
        if (!canAttack(attacker, attacked))
            return;
        int damage = getAttack.getAttack(attacker).damage();
        int healthLeft = attacked.damage(damage).orElse(0);
        sendAttack(attacker, attacked, damage);
        if (healthLeft <= 0)
            kill(attacked);
    }

    private boolean canAttack(Entity attacker, Entity attacked) {
        if (!state.containsEntity(attacker.id()) ||
            !state.containsEntity(attacked.id()))
            return false;
        else if (getAttack.getAttack(attacker) == null || attacked.getHealth().isEmpty())
            return false;
        else if (getAttack.getAttack(attacker) == null ||
                 attacked.getHealth().get().getCurrentHealth() <= 0)
            return false;
        else
            return true;
    }

    private void kill(Entity entity) {
        Position position = state.entityPosition(entity.id());
        RemovedEntity removedEntity = state.removeEntity(entity.id());
        sendKill(entity, position, removedEntity);
    }

    private void sendKill(Entity entity, Position position, RemovedEntity removedEntity) {
        for (PlayerID player : state.players()) {
            if (player.equals(entity.owner()))
                sender.send(ownerKillEvent(entity, removedEntity), player);
            else if (state.playerSees(player, position))
                sender.send(otherKillEvent(player, entity, removedEntity), player);
        }
    }

    private Event otherKillEvent(PlayerID player, Entity entity, RemovedEntity removedEntity) {
        return new KillEntity(
                entity.id(),
                VisibilityChange.empty(),
                state.maskedFor(player, removedEntity.claimChange())
        );
    }

    private Event ownerKillEvent(Entity entity, RemovedEntity removedEntity) {
        return new KillEntity(
                entity.id(),
                removedEntity.visibilityChange(),
                removedEntity.claimChange()
        );
    }

    private void sendAttack(Entity attacker, Entity attacked, int damage) {
        for (PlayerID player : state.players())
            sendAttack(player, attacker, attacked, damage);
    }

    private void sendAttack(PlayerID player, Entity attacker, Entity attacked, int damage) {
        Position attackerPosition = state.entityPosition(attacker.id());
        Position attackedPosition = state.entityPosition(attacked.id());

        if (seesBothParties(player, attackerPosition, attackedPosition))
            sender.send(new AttackEntityEvent(attacker.id(), attacked.id(), damage), player);
        else if (state.playerSees(player, attackerPosition))
            sender.send(new AttackPosition(attacker.id(), attackedPosition), player);
        else if (state.playerSees(player, attackedPosition))
            sender.send(new DamageEntity(attacked.id(), damage), player);
    }

    private boolean seesBothParties(
            PlayerID player, Position attackerPosition, Position attackedPosition
    ) {
        return state.playerSees(player, attackerPosition) &&
               state.playerSees(player, attackedPosition);
    }
}
