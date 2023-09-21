package mudgame.client.events;

import core.claiming.ClaimChange;
import core.claiming.ClaimedAreaView.ClaimedPosition;
import core.model.Position;
import lombok.RequiredArgsConstructor;
import mudgame.client.ClientGameState;
import mudgame.controls.events.ClaimChanges;

@RequiredArgsConstructor
public class ClaimEventProcessor {
    private final ClientGameState state;

    public void claimChange(ClaimChanges e) {
        for (ClaimChange claimChange : e.claimChanges()) {
            for (ClaimedPosition p : claimChange.claimedPositions())
                state.claimedArea().claim(p.owner(), p.position());
            for (Position p : claimChange.unclaimedPositions())
                state.claimedArea().unclaim(p);
        }
    }
}
