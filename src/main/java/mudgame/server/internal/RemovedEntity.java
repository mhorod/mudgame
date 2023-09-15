package mudgame.server.internal;

import core.claiming.ClaimedAreaView.ClaimChange;
import core.model.EntityID;
import mudgame.controls.events.VisibilityChange;

public record RemovedEntity(
        EntityID entityID,
        VisibilityChange visibilityChange,
        ClaimChange claimChange
) { }
