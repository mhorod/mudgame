package core.model;

import java.io.Serializable;

public record Position(int x, int y) implements Serializable {
    public Position plus(int dx, int dy) {
        return new Position(x + dx, y + dy);
    }
}
