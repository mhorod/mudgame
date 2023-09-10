package core.turns;

import core.model.PlayerID;
import org.junit.jupiter.api.Test;

import java.util.LinkedList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;


class TurnManagerTest {
    @Test
    void playerManager_returns_as_many_player_ids_as_there_are_players() {
        // given
        TurnManager turnManager = new TurnManager(4);

        // when
        List<PlayerID> playerIDs = turnManager.getPlayerIDs();

        // then
        assertThat(playerIDs).hasSize(4);
    }

    @Test
    void playerManager_throws_exception_when_created_with_non_positive_player_count() {
        assertThatThrownBy(() -> new TurnManager(0)).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> new TurnManager(-1)).isInstanceOf(
                IllegalArgumentException.class);
    }

    @Test
    void player_ids_are_unique() {
        // given
        TurnManager turnManager = new TurnManager(4);

        // when
        List<PlayerID> playerIDs = turnManager.getPlayerIDs();

        // then
        assertThat(playerIDs).doesNotHaveDuplicates();
    }

    @Test
    void completing_turn_switches_current_player() {
        // given
        TurnManager turnManager = new TurnManager(2);
        PlayerID first = turnManager.getCurrentPlayer();

        // when
        turnManager.completeTurn();

        // then
        assertThat(turnManager.getCurrentPlayer()).isNotEqualTo(first);
    }

    @Test
    void turn_cycles_back_to_starting_player_after_each_player_completes_turn() {
        // given
        TurnManager turnManager = new TurnManager(3);
        PlayerID first = turnManager.getCurrentPlayer();

        // when
        for (int i = 0; i < 3; i++)
            turnManager.completeTurn();

        // then
        assertThat(turnManager.getCurrentPlayer()).isEqualTo(first);
    }

    @Test
    void each_player_takes_turn_once_during_one_cycle() {
        // given
        TurnManager turnManager = new TurnManager(3);
        List<PlayerID> playerIDs = turnManager.getPlayerIDs();

        // when
        List<PlayerID> turns = new LinkedList<>();

        for (int i = 0; i < 3; i++) {
            turns.add(turnManager.getCurrentPlayer());
            turnManager.completeTurn();
        }

        // then
        assertThat(turns).containsAll(playerIDs);
    }

}
