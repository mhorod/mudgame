package io;

public record Tile(TileKind kind, float x, float y) {
    public static final float ASPECT_RATIO = 128.0f / 74.0f;
}
