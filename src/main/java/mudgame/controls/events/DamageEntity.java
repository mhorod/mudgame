package mudgame.controls.events;

import core.event.Event;
import core.model.EntityID;

/**
 * Sent when the receiver sees the attacked entity but not the attacker
 */
public record DamageEntity(EntityID attacked, int damage) implements Event {
}
