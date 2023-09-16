package mudgame.controls.events;

import core.claiming.ClaimChange;
import core.model.EntityID;

/**
 * Remove entity due to a loss of Health
 */
public record KillEntity(
        EntityID entityID,
        VisibilityChange visibilityChange,
        ClaimChange claimChange
) implements Event {
}
