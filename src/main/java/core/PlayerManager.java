package core;

import core.model.PlayerID;

import java.util.List;
import java.util.stream.IntStream;

public class PlayerManager
{
    private final int playerCount;
    private final List<PlayerID> playerIDs;
    private int currentTurn;

    public PlayerManager(int playerCount)
    {
        if (playerCount <= 0)
            throw new IllegalArgumentException("playerCount must be positive");

        this.playerCount = playerCount;
        this.playerIDs = IntStream.range(0, playerCount).mapToObj(PlayerID::new).toList();
        currentTurn = 0;
    }

    public List<PlayerID> getPlayerIDs()
    {
        return playerIDs;
    }

    public PlayerID getCurrentPlayer() { return playerIDs.get(currentTurn); }

    public void setCurrentPlayer(PlayerID player)
    {
        currentTurn = playerIDs.indexOf(player);
    }

    public void completeTurn()
    {
        currentTurn = nextTurn();
    }

    private int nextTurn() { return (currentTurn + 1) % playerCount; }
}
