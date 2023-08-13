package core;

import core.id.PlayerID;
import org.junit.jupiter.api.Test;

import java.util.LinkedList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;


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
        assertThat(playerIDs).hasSize(4);
    }

    @Test
    void core_throws_exception_when_created_with_non_positive_player_count()
    {
        assertThatThrownBy(() -> new GameCore(0)).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> new GameCore(-1)).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void player_ids_are_unique()
    {
        // given
        GameCore core = new GameCore(4);

        // when
        List<PlayerID> playerIDs = core.getPlayerIDs();

        // then
        assertThat(playerIDs).doesNotHaveDuplicates();
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
        assertThat(core.getCurrentPlayer()).isNotEqualTo(first);
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
        assertThat(core.getCurrentPlayer()).isEqualTo(first);
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
        assertThat(turns).containsAll(playerIDs);
    }

}
