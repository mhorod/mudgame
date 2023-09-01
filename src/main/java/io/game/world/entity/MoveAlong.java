package io.game.world.entity;

import core.model.Position;
import io.game.world.arrow.ArrowKind;

import java.util.List;
import java.util.stream.IntStream;

public class MoveAlong extends AnimationChain {
    public MoveAlong(List<Position> path) {
        super(fromPath(path));
    }

    private static List<EntityAnimation> fromPath(List<Position> path) {
        var dirs = ArrowKind.fromPositions(path);
        return IntStream.range(0, path.size())
                .mapToObj(i -> (EntityAnimation) new Move(path.get(i), dirs.get(i).from(), dirs.get(i).to()))
                .toList();
    }
}
