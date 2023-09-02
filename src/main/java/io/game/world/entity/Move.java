package io.game.world.entity;

import core.model.Position;
import io.game.WorldPosition;
import io.game.world.arrow.Direction;

import java.util.ArrayList;
import java.util.List;

public class Move extends AnimationChain {
    private static final float SPEED = 3;


    public Move(Position tile, Direction from, Direction to) {
        super(create(tile, from, to));
    }

    private static List<EntityAnimation> create(Position tile, Direction from, Direction to) {
        List<EntityAnimation> partials = new ArrayList<>();
        if (from != Direction.NONE)
            partials.add(new Step(
                    new WorldPosition(tile.x() + from.dx / 2f, tile.y() + from.dy / 2f, 0),
                    WorldPosition.from(tile),
                    0.5f / SPEED
            ));
        if (to != Direction.NONE)
            partials.add(new Step(
                    WorldPosition.from(tile),
                    new WorldPosition(tile.x() + to.dx / 2f, tile.y() + to.dy / 2f, 0),
                    0.5f / SPEED
            ));
        return partials;
    }
}
