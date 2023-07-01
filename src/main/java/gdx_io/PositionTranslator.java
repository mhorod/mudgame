package gdx_io;

import io.ScreenPosition;

public interface PositionTranslator {
    /**
     * translates position from pixel coordinates to in-game coordinates x -> [0, 1], y -> [0, height / width].
     */
    ScreenPosition translate(int x, int y);
}
