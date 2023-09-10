package mudgame.controls.events;

import core.claiming.ClaimedAreaView.ClaimChange;
import core.event.Event;
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
