package io.game.world.arrow;

import core.model.Position;

public enum Direction {
    NONE(0, 0), SE(1, 0), SW(0, 1), NE(0, -1), NW(-1, 0);
    public final int dx;
    public final int dy;

    Direction(int dx, int dy) {
        this.dx = dx;
        this.dy = dy;
    }

    public Direction getOpposite() {
        return switch (this) {
            case NONE -> NONE;
            case SE -> NW;
            case SW -> NE;
            case NE -> SW;
            case NW -> SE;
        };
    }

    public static Direction between(Position a, Position b) {
        var dx = b.x() - a.x();
        var dy = b.y() - a.y();
        if (dx == 0 && dy == 0)
            return Direction.NONE;
        if (dx == 1 && dy == 0)
            return Direction.SE;
        if (dx == 0 && dy == 1)
            return Direction.SW;
        if (dx == -1 && dy == 0)
            return Direction.NW;
        if (dx == 0 && dy == -1)
            return Direction.NE;
        throw new RuntimeException("Positions are not neighboring.");
    }
}
