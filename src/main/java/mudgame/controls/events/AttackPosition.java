package mudgame.controls.events;

import core.model.EntityID;
import core.model.Position;

/**
 * Sent when receiver sends only the attacker entity
 */
public record AttackPosition(EntityID attacker, Position position) implements Event {
}
