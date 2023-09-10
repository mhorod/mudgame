package mudgame.controls.events;

import core.event.Event;
import core.model.PlayerID;

public record SetTurn(PlayerID currentPlayer) implements Event {
}
