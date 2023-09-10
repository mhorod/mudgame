package core.claiming;

import java.util.HashSet;

import static org.assertj.core.api.Assertions.assertThat;

public class PlayerClaimedAreaAssert {
    private final PlayerClaimedArea actual;

    private PlayerClaimedAreaAssert(PlayerClaimedArea actual) { this.actual = actual; }

    public static PlayerClaimedAreaAssert assertThatPlayerClaimedArea(PlayerClaimedArea actual) {
        return new PlayerClaimedAreaAssert(actual);
    }

    public PlayerClaimedAreaAssert isEqualTo(PlayerClaimedArea expected) {
        assertThat(new HashSet<>(actual.claimedPositions())).isEqualTo(
                new HashSet<>(expected.claimedPositions()));
        return this;
    }
}
