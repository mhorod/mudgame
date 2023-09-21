package io.game.world.entity;

import core.model.Position;

import java.util.List;

public class Attack extends AnimationChain {

    public Attack(Position start, Position destination) {
        super(List.of(new JumpTo(destination), new JumpTo(start)));
    }
}
