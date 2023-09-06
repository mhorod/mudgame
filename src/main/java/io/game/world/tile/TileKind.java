package io.game.world.tile;

import core.terrain.model.TerrainType;

public enum TileKind {
    TILE_DARK,
    TILE_LIGHT,
    FOG,
    NONE;

    public static TileKind from(TerrainType type) {
        return switch (type) {
            case UNKNOWN -> FOG;
            case VOID -> NONE;
            case WATER -> TILE_LIGHT;
            case LAND, MOUNTAIN -> TILE_DARK;
        };
    }
}
