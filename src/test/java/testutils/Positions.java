package testutils;

import core.model.Position;
import lombok.experimental.UtilityClass;

@UtilityClass
public class Positions {
    public static Position pos(int x, int y) {
        return new Position(x, y);
    }
}
