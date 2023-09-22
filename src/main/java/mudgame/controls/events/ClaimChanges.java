package mudgame.controls.events;

import core.claiming.ClaimChange;

import java.util.List;

public record ClaimChanges(List<ClaimChange> claimChanges) implements Event {
}
