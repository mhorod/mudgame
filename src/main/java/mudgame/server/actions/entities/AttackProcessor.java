package mudgame.server.actions.entities;

import core.claiming.ClaimChange;
import core.entities.model.Entity;
import core.entities.model.components.Attack;
import core.entities.model.components.visitors.GetAttack;
import core.entities.model.components.visitors.GetHealth;
import core.model.PlayerID;
import core.model.Position;
import core.resources.Resources;
import mudgame.controls.actions.AttackEntityAction;
import mudgame.controls.events.AttackEntityEvent;
import mudgame.controls.events.AttackPosition;
import mudgame.controls.events.ClaimChanges;
import mudgame.controls.events.DamageEntity;
import mudgame.controls.events.Event;
import mudgame.controls.events.KillEntity;
import mudgame.controls.events.ProduceResources;
import mudgame.controls.events.VisibilityChange;
import mudgame.server.actions.EventSender;
import mudgame.server.internal.InteractiveState;
import mudgame.server.internal.RemovedEntity;

import java.util.List;
import java.util.Map;

import static core.resources.ResourceType.MUD;

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
        attacker.getAttack().ifPresent(Attack::attack);
        int damage = attacker.getAttack().map(Attack::damage).orElse(0);
        int healthLeft = attacked.damage(damage).orElse(0);
        sendAttack(attacker, attacked, damage);
        if (healthLeft <= 0) {
            kill(attacked);
            sendKillReward(attacker.owner(), attacked);
        }
    }

    private void sendKillReward(PlayerID owner, Entity attacked) {
        int mud = switch (attacked.type()) {
            case PAWN -> 1;
            case MARSH_WIGGLE -> 2;
            case WARRIOR -> 3;
            case TOWER -> 5;
            case BASE -> 20;
        };

        Resources resources = Resources.of(Map.of(MUD, mud));
        state.addResources(owner, resources);
        Event event = new ProduceResources(resources);
        sender.send(event, owner);
    }

    private boolean canAttack(Entity attacker, Entity attacked) {
        if (!state.containsEntity(attacker.id()) ||
            !state.containsEntity(attacked.id()))
            return false;
        else if (attacker.getAttack().isEmpty())
            return false;
        else if (attacked.getHealth().isEmpty())
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
            else {
                ClaimChange claimChange = removedEntity.claimChange()
                        .masked(state.playerFow(player), state.terrain());
                if (!claimChange.isEmpty())
                    sender.send(new ClaimChanges(List.of(claimChange)), player);
            }
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
