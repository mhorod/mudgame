package core.turns;

import core.model.PlayerID;
import org.junit.jupiter.api.Test;

import java.util.LinkedList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;


class PlayerManagerTest {
    @Test
    void playerManager_returns_as_many_player_ids_as_there_are_players() {
        // given
        PlayerManager playerManager = new PlayerManager(4);

        // when
        List<PlayerID> playerIDs = playerManager.getPlayerIDs();

        // then
        assertThat(playerIDs).hasSize(4);
    }

    @Test
    void playerManager_throws_exception_when_created_with_non_positive_player_count() {
        assertThatThrownBy(() -> new PlayerManager(0)).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> new PlayerManager(-1)).isInstanceOf(
                IllegalArgumentException.class);
    }

    @Test
    void player_ids_are_unique() {
        // given
        PlayerManager playerManager = new PlayerManager(4);

        // when
        List<PlayerID> playerIDs = playerManager.getPlayerIDs();

        // then
        assertThat(playerIDs).doesNotHaveDuplicates();
    }

    @Test
    void completing_turn_switches_current_player() {
        // given
        PlayerManager playerManager = new PlayerManager(2);
        PlayerID first = playerManager.getCurrentPlayer();

        // when
        playerManager.completeTurn();

        // then
        assertThat(playerManager.getCurrentPlayer()).isNotEqualTo(first);
    }

    @Test
    void turn_cycles_back_to_starting_player_after_each_player_completes_turn() {
        // given
        PlayerManager playerManager = new PlayerManager(3);
        PlayerID first = playerManager.getCurrentPlayer();

        // when
        for (int i = 0; i < 3; i++)
            playerManager.completeTurn();

        // then
        assertThat(playerManager.getCurrentPlayer()).isEqualTo(first);
    }

    @Test
    void each_player_takes_turn_once_during_one_cycle() {
        // given
        PlayerManager playerManager = new PlayerManager(3);
        List<PlayerID> playerIDs = playerManager.getPlayerIDs();

        // when
        List<PlayerID> turns = new LinkedList<>();

        for (int i = 0; i < 3; i++) {
            turns.add(playerManager.getCurrentPlayer());
            playerManager.completeTurn();
        }

        // then
        assertThat(turns).containsAll(playerIDs);
    }

}
