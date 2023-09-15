package mudgame.server.internal;

import core.claiming.ClaimedAreaView.ClaimChange;
import core.entities.model.Entity;
import mudgame.controls.events.VisibilityChange;

public record CreatedEntity(
        Entity entity,
        VisibilityChange visibilityChange,
        ClaimChange claimChange
) { }
