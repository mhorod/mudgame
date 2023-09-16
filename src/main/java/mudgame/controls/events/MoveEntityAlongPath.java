package mudgame.controls.events;

import core.claiming.ClaimedAreaView.ClaimChange;
import core.model.EntityID;
import core.model.Position;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;

/**
 * This event represents a movement of an entity along given path.
 * Since entities have vision this event also carries information of how
 * to update fog of war (if the receiving player owns the entity).
 * <p>
 * Due to fog of war it may happen that entity temporarily disappears
 * in the middle of the path - in such case the {@link SingleMove#destination()}
 * is an empty optional indicating that the entity is moving but the player
 * has no information where until the unit appears again.
 */
public record MoveEntityAlongPath(EntityID entityID, List<SingleMove> moves) implements Event {
    public record SingleMove(
            Position destinationNullable,
            VisibilityChange visibilityChange,
            ClaimChange claimChange
    ) implements Serializable {
        public Optional<Position> destination() {
            return Optional.ofNullable(destinationNullable);
        }

        public boolean isHidden() {
            return destinationNullable == null;
        }

        public boolean isShown() {
            return destinationNullable != null;
        }
    }
}
