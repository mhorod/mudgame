package core.fogofwar;

import lombok.RequiredArgsConstructor;

import java.util.HashSet;

import static org.assertj.core.api.Assertions.assertThat;

@RequiredArgsConstructor
public class PlayerFogOfWarAssert {
    private final PlayerFogOfWar actual;

    public static PlayerFogOfWarAssert assertThatPlayerFow(PlayerFogOfWar actual) {
        return new PlayerFogOfWarAssert(actual);
    }

    public PlayerFogOfWarAssert isEqualTo(PlayerFogOfWar expected) {
        assertThat(new HashSet<>(actual.visiblePositions()))
                .isEqualTo(new HashSet<>(expected.visiblePositions()));
        assertThat(actual).isEqualTo(expected);
        return this;
    }
}
