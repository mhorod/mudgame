package io.game.world.entity;

import core.model.Position;
import io.game.world.arrow.Arrow;

import java.util.List;

public class MoveAlong extends AnimationChain {
    public MoveAlong(List<Position> path) {
        super(fromPath(path));
    }

    private static List<EntityAnimation> fromPath(List<Position> path) {
        return Arrow.fromPositions(path).stream()
                .map(arrow -> (EntityAnimation) new Move(arrow.position(), arrow.kind().from(), arrow.kind().to()))
                .toList();
    }
}
