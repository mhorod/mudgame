package io.game.world.arrow;

import core.model.Position;
import io.game.world.WorldTexture;

import java.util.ArrayList;
import java.util.List;

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

    public static List<ArrowKind> fromPositions(List<Position> positions) {
        if (positions.isEmpty()) return List.of();
        if (positions.size() == 1) return List.of(new ArrowKind(Direction.NONE, Direction.NONE));
        ArrayList<ArrowKind> result = new ArrayList<>();
        result.add(new ArrowKind(Direction.NONE, Direction.between(positions.get(0), positions.get(1))));
        for (int i = 1; i < positions.size() - 1; i++) {
            var prev = positions.get(i - 1);
            var cur = positions.get(i);
            var next = positions.get(i + 1);
            result.add(new ArrowKind(Direction.between(cur, prev), Direction.between(cur, next)));
        }
        result.add(new ArrowKind(
                Direction.between(
                        positions.get(positions.size() - 1),
                        positions.get(positions.size() - 2)
                ),
                Direction.NONE
        ));
        return result;
    }

}
