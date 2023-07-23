package core;

import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;


class GameCoreTest
{
    @Test
    void core_returns_as_many_player_ids_as_there_are_players()
    {
        // given
        GameCore core = new GameCore(4);

        // when
        List<PlayerID> playerIDs = core.getPlayerIDs();

        // then
        assertEquals(4, playerIDs.size());
    }

    @Test
    void core_throws_exception_when_created_with_non_positive_player_count()
    {
        assertThrows(IllegalArgumentException.class, () -> new GameCore(0));
        assertThrows(IllegalArgumentException.class, () -> new GameCore(-1));
    }

    @Test
    void player_ids_are_unique()
    {
        // given
        GameCore core = new GameCore(4);

        // when
        List<PlayerID> playerIDs = core.getPlayerIDs();

        // then
        Set<PlayerID> uniquePlayerIDs = new HashSet<>(playerIDs);
        assertEquals(4, uniquePlayerIDs.size());
    }

    @Test
    void completing_turn_switches_current_player()
    {
        // given
        GameCore core = new GameCore(2);
        PlayerID first = core.getCurrentPlayer();

        // when
        core.completeTurn();

        // then
        PlayerID current = core.getCurrentPlayer();
        assertNotEquals(first, current);
    }

    @Test
    void turn_cycles_back_to_starting_player_after_each_player_completes_turn()
    {
        // given
        GameCore core = new GameCore(3);
        PlayerID first = core.getCurrentPlayer();

        // when
        for (int i = 0; i < 3; i++)
            core.completeTurn();

        // then
        PlayerID current = core.getCurrentPlayer();
        assertEquals(first, current);
    }

    @Test
    void each_player_takes_turn_once_during_one_cycle()
    {
        // given
        GameCore core = new GameCore(3);
        List<PlayerID> playerIDs = core.getPlayerIDs();

        // when
        List<PlayerID> turns = new LinkedList<>();

        for (int i = 0; i < 3; i++)
        {
            turns.add(core.getCurrentPlayer());
            core.completeTurn();
        }

        // then
        assertTrue(turns.containsAll(playerIDs));
    }

}
