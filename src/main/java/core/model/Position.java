package core.model;

import java.io.Serializable;

public record Position(int x, int y) implements Serializable {
    public Position plus(int dx, int dy) {
        return new Position(x + dx, y + dy);
    }

    public int movementDistance(Position position) {
        return Math.abs(x - position.x()) + Math.abs(y - position.y());
    }

    public int attackDistanceSquare(Position position) {
        int dx = x - position.x();
        int dy = y - position.y();
        return dx * dx + dy * dy;
    }
}
