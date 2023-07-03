package io;

public enum Direction {
    NONE(0, 0), SE(1, 0), SW(0, 1), NE(0, -1), NW(-1, 0);
    public final int dx;
    public final int dy;
    Direction(int dx, int dy) {
        this.dx = dx;
        this.dy = dy;
    }

    Direction getOpposite() {
        return switch(this) {
            case NONE -> NONE;
            case SE -> NW;
            case SW -> NE;
            case NE -> SW;
            case NW -> SE;
        };
    }
}
