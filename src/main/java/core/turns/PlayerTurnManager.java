package core.turns;

import core.model.PlayerID;

import java.io.Serializable;

public class PlayerTurnManager implements PlayerTurnView, Serializable {
    private final PlayerID myPlayerID;
    private PlayerID currentPlayerID;
    private int currentTurn = 0;
    private int playerCount = 0;

    public PlayerTurnManager(
            PlayerID myPlayerID, PlayerID currentPlayerID, int currentTurn, int playerCount
    ) {
        this.myPlayerID = myPlayerID;
        this.currentPlayerID = currentPlayerID;
        this.currentTurn = currentTurn;
        this.playerCount = playerCount;
    }


    @Override
    public boolean isMyTurn() {
        return myPlayerID.equals(currentPlayerID);
    }

    @Override
    public PlayerID currentPlayer() {
        return currentPlayerID;
    }

    @Override
    public int currentTurn() {
        return currentTurn;
    }

    @Override
    public int playerCount() {
        return playerCount;
    }

    public void nextTurn(PlayerID playerID) {
        currentPlayerID = playerID;
        currentTurn++;
    }
}
