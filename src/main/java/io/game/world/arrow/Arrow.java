package io.game.world.arrow;

import core.model.Position;
import io.game.Camera;
import io.game.WorldPosition;
import io.model.engine.Canvas;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

public record Arrow(Position position, ArrowKind kind) {

    public void draw(Canvas canvas, Camera camera) {
        kind().getTexture().draw(WorldPosition.from(position()), canvas, camera);
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
