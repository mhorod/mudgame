package mudgame.controls.events;

import core.event.Event;
import core.model.EntityID;

/**
 * Sent when receiver sees both the attacker and the attacked entities
 */
public record AttackEntityEvent(EntityID attacker, EntityID attacked, int damage) implements Event {
}
