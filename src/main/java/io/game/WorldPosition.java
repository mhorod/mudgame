package io.game;

import core.model.Position;

public record WorldPosition(float x, float y, float z) {
    public static WorldPosition from(Position pos) {
        return new WorldPosition(pos.x(), pos.y(), 0);
    }

    public static WorldPosition from(Position pos, float z) {
        return new WorldPosition(pos.x(), pos.y(), z);
    }
}
