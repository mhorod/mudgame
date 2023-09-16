package mudgame.controls.events;

import core.model.PlayerID;

public record NextTurn(PlayerID currentPlayer) implements Event {
}
