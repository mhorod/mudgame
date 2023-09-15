package mudgame.controls.events;

import core.event.Event;
import core.model.PlayerID;

public record NextTurn(PlayerID currentPlayer) implements Event {
}
