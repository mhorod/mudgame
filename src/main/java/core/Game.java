package core;

import java.util.List;
import java.util.stream.IntStream;

public class Game
{
    private final int playerCount;
    private final List<PlayerID> playerIDs;
    private int currentTurn;


    public Game(int playerCount)
    {
        this.playerCount = playerCount;
        this.playerIDs = IntStream.range(0, playerCount)
                .mapToObj(PlayerID::new)
                .toList();

        currentTurn = 0;
    }

    public List<PlayerID> getPlayerIDs()
    {
        return playerIDs;
    }

    public PlayerID getCurrentPlayer() { return playerIDs.get(currentTurn); }

    public void setCurrentPlayer(
            PlayerID player
    )
    {
        currentTurn = playerIDs.indexOf(player);
    }


    public List<Event> completeTurn(PlayerID player)
    {
        currentTurn = nextTurn();
        return List.of();
    }

    public List<Event> placeUnit(
            PlayerID player, Position position, Unit unit
    )
    {
        return List.of();
    }


    private int nextTurn() { return (currentTurn + 1) % playerCount; }

    public static class PlayerID
    {
        private int id;

        private PlayerID(int id) { this.id = id; }
    }

    public static class UnitID
    {
        private int id;

        private UnitID(int id) { this.id = id; }
    }

}
