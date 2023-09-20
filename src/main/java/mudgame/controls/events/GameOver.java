package mudgame.controls.events;

import core.model.PlayerID;

import java.util.List;

public record GameOver(List<PlayerID> winners) implements Event {
}
