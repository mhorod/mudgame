package testutils;

import core.entities.model.Entity;
import core.model.EntityID;
import core.model.PlayerID;
import core.model.Position;
import lombok.experimental.UtilityClass;
import mudgame.controls.actions.Action;
import mudgame.controls.actions.AttackEntityAction;
import mudgame.controls.actions.CompleteTurn;
import mudgame.controls.actions.CreateEntity;
import mudgame.controls.actions.MoveEntity;

import static core.entities.model.EntityType.MARSH_WIGGLE;
import static core.entities.model.EntityType.PAWN;

@UtilityClass
public class Actions {
    public static Action move(Entity entity, Position position) {
        return new MoveEntity(entity.id(), position);
    }

    public static Action createPawn(PlayerID player, Position position) {
        return new CreateEntity(PAWN, player, position);
    }

    public static Action createMarshWiggle(PlayerID player, Position position) {
        return new CreateEntity(MARSH_WIGGLE, player, position);
    }

    public static Action attack(EntityID attacker, EntityID attacked) {
        return new AttackEntityAction(attacker, attacked);
    }

    public static Action attack(Entity attacker, Entity attacked) {
        return new AttackEntityAction(attacker.id(), attacked.id());
    }

    public static Action completeTurn() {
        return new CompleteTurn();
    }

}
