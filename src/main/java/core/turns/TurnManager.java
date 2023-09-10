package core.turns;

import core.model.PlayerID;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.List;
import java.util.stream.IntStream;

@EqualsAndHashCode
public final class TurnManager implements TurnView, Serializable {
    private final int playerCount;
    private final List<PlayerID> playerIDs;
    private int currentTurn;

    public TurnManager(int playerCount) {
        if (playerCount <= 0)
            throw new IllegalArgumentException("playerCount must be positive");

        this.playerCount = playerCount;
        this.playerIDs = IntStream.range(0, playerCount).mapToObj(PlayerID::new).toList();
        currentTurn = 0;
    }

    public List<PlayerID> players() {
        return playerIDs;
    }

    public PlayerID currentPlayer() { return playerIDs.get(currentTurn); }

    public void completeTurn() {
        currentTurn = nextTurn();
    }

    public void setTurn(int turn) { currentTurn = turn; }

    private int nextTurn() { return (currentTurn + 1) % playerCount; }
}
