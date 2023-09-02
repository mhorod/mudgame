package io.game.world.arrow;

import core.model.Position;
import io.game.Camera;
import io.game.WorldPosition;
import io.game.world.WorldEntity;
import io.model.engine.Canvas;

import java.util.List;
import java.util.stream.IntStream;

public record Arrow(Position position, ArrowKind kind) implements WorldEntity {

    @Override
    public void draw(Canvas canvas, Camera camera) {
        kind().getTexture().draw(getPosition(), canvas, camera);
    }

    @Override
    public WorldPosition getPosition() {
        return new WorldPosition(position().x(), position().y(), 0);
    }

    public static List<Arrow> fromPositions(List<Position> positions) {
        var kinds = ArrowKind.fromPositions(positions);
        return IntStream.range(0, positions.size())
                .mapToObj(i -> new Arrow(positions.get(i), kinds.get(i)))
                .toList();
    }
}
