package core.turns;

import core.model.PlayerID;

import java.io.Serializable;

public class PlayerTurnManager implements PlayerTurnView, Serializable {
    private final PlayerID myPlayerID;
    private PlayerID currentPlayerID;

    public PlayerTurnManager(PlayerID myPlayerID, PlayerID currentPlayerID) {
        this.myPlayerID = myPlayerID;
        this.currentPlayerID = currentPlayerID;
    }


    @Override
    public boolean isMyTurn() {
        return myPlayerID.equals(currentPlayerID);
    }

    @Override
    public PlayerID getCurrentPlayer() {
        return currentPlayerID;
    }

    public void setTurn(PlayerID playerID) {
        currentPlayerID = playerID;
    }
}
