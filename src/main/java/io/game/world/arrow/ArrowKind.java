package io.game.world.arrow;

import io.game.world.WorldTexture;

public record ArrowKind(Direction from, Direction to) {
    public WorldTexture getTexture() {
        return switch (from) {
            case NONE -> switch (to) {
                case NONE -> WorldTexture.ARROW_NONE;
                case SE -> WorldTexture.ARROW_START_SE;
                case SW -> WorldTexture.ARROW_START_SW;
                case NE -> WorldTexture.ARROW_START_NE;
                case NW -> WorldTexture.ARROW_START_NW;
            };
            case SE -> switch (to) {
                case NONE -> WorldTexture.ARROW_END_SE;
                case SE -> WorldTexture.ARROW_NONE;
                case SW -> WorldTexture.ARROW_SW_SE;
                case NE -> WorldTexture.ARROW_SE_NE;
                case NW -> WorldTexture.ARROW_SE_NW;
            };
            case SW -> switch (to) {
                case NONE -> WorldTexture.ARROW_END_SW;
                case SE -> WorldTexture.ARROW_SW_SE;
                case SW -> WorldTexture.ARROW_NONE;
                case NE -> WorldTexture.ARROW_SW_NE;
                case NW -> WorldTexture.ARROW_SW_NW;
            };
            case NE -> switch (to) {
                case NONE -> WorldTexture.ARROW_END_NE;
                case SE -> WorldTexture.ARROW_SE_NE;
                case SW -> WorldTexture.ARROW_SW_NE;
                case NE -> WorldTexture.ARROW_NONE;
                case NW -> WorldTexture.ARROW_NW_NE;
            };
            case NW -> switch (to) {
                case NONE -> WorldTexture.ARROW_END_NW;
                case SE -> WorldTexture.ARROW_SE_NW;
                case SW -> WorldTexture.ARROW_SW_NW;
                case NE -> WorldTexture.ARROW_NW_NE;
                case NW -> WorldTexture.ARROW_NONE;
            };
        };
    }
}
