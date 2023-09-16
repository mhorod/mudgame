package core.turns;

import core.model.PlayerID;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;

@Slf4j
public class PlayerTurnManager implements PlayerTurnView, Serializable {
    private final PlayerID myPlayerID;
    private PlayerID currentPlayerID;
    private int currentTurn;
    private final int playerCount;

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
