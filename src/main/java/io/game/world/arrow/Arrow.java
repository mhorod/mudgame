package io.game.world.arrow;

import core.model.Position;
import io.game.WorldPosition;
import io.game.world.WorldEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

public class Arrow extends WorldEntity {
    private final Position gamePosition;
    private final ArrowKind kind;

    public Arrow(Position position, ArrowKind kind) {
        super(WorldPosition.from(position), kind.getTexture(), 0);
        this.gamePosition = position;
        this.kind = kind;
    }

    public ArrowKind getKind() {
        return kind;
    }

    public Position getGamePosition() {
        return gamePosition;
    }

    public static List<Arrow> fromPositions(List<Position> positions) {
        var kinds = ArrowKind.fromPositions(positions);
        return IntStream.range(0, positions.size())
                .mapToObj(i -> new Arrow(positions.get(i), kinds.get(i)))
                .toList();
    }


    public static List<Position> pathBetween(Position a, Position b) {
        ArrayList<Position> result = new ArrayList<>();
        while (!a.equals(b)) {
            result.add(a);
            var dx = b.x() - a.x();
            var dy = b.y() - a.y();
            if (dx > 0)
                a = new Position(a.x() + 1, a.y());
            else if (dx < 0)
                a = new Position(a.x() - 1, a.y());
            else if (dy > 0)
                a = new Position(a.x(), a.y() + 1);
            else if (dy < 0)
                a = new Position(a.x(), a.y() - 1);
        }
        result.add(b);
        return result;
    }
}
