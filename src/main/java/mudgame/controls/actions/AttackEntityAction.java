package mudgame.controls.actions;

import core.event.Action;
import core.model.EntityID;

public record AttackEntityAction(EntityID attacker, EntityID attacked) implements Action {
}
