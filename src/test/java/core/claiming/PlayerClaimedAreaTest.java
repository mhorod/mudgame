package core.claiming;

import core.claiming.ClaimedAreaView.ClaimedPosition;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static testutils.Players.PLAYER_0;
import static testutils.Players.PLAYER_1;
import static testutils.Positions.pos;

class PlayerClaimedAreaTest {
    @Test
    void new_instance_contains_no_claimed_area() {
        // given
        PlayerClaimedArea testee = new PlayerClaimedArea();

        // when
        List<ClaimedPosition> claimedPositions = testee.claimedPositions();

        // then
        assertThat(claimedPositions).isEmpty();
    }

    @Test
    void player_can_claim_area() {
        // given
        PlayerClaimedArea testee = new PlayerClaimedArea();

        // when
        testee.claim(PLAYER_0, List.of(pos(0, 0), pos(1, 1)));
        List<ClaimedPosition> claimedPositions = testee.claimedPositions();

        // then
        assertThat(claimedPositions).contains(
                new ClaimedPosition(pos(0, 0), PLAYER_0),
                new ClaimedPosition(pos(1, 1), PLAYER_0)
        );
    }

    @Test
    void claiming_terrain_overrides_owner() {
        // given
        PlayerClaimedArea testee = new PlayerClaimedArea();

        // when
        testee.claim(PLAYER_0, List.of(pos(0, 0), pos(1, 1)));
        testee.claim(PLAYER_1, List.of(pos(0, 0)));
        List<ClaimedPosition> claimedPositions = testee.claimedPositions();

        // then
        assertThat(claimedPositions).contains(
                new ClaimedPosition(pos(0, 0), PLAYER_1),
                new ClaimedPosition(pos(1, 1), PLAYER_0)
        );
    }

    @Test
    void unclaimed_terrain_is_not_claimed() {
        // given
        PlayerClaimedArea testee = new PlayerClaimedArea();

        // when
        testee.claim(PLAYER_0, List.of(pos(0, 0), pos(1, 1)));
        testee.unclaim(List.of(pos(0, 0)));
        List<ClaimedPosition> claimedPositions = testee.claimedPositions();

        // then
        assertThat(claimedPositions).contains(
                new ClaimedPosition(pos(1, 1), PLAYER_0)
        );
    }
}