package core.turns;

import core.model.PlayerID;

public interface TurnView {
    PlayerID currentPlayer();
    int currentTurn();
    int playerCount();
}
