package io.model.engine;

import core.model.PlayerID;

public enum Color {
    WHITE, PINK, GREEN, BLUE, RED, CYAN, MAGENTA, YELLOW, ORANGE, PURPLE;

    public static Color fromPlayerId(PlayerID id) {
        return Color.values()[(int) id.id() + 1];
    }
}
