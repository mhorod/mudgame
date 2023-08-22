package io;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.function.Consumer;

public class MapView implements Drawable {
    private final int width;
    private final int height;

    public float scale;
    public ScreenPosition offset;

    private final Arrow[][] arrows_map;

    public MapView(int width, int height, float scale, ScreenPosition offset) {
        this.width = width;
        this.height = height;
        this.scale = scale;
        this.offset = offset;
        arrows_map = new Arrow[width][height];
        for (var row : arrows_map)
            Arrays.fill(row, null);
        makeArrow(new GamePosition(0, 0), new GamePosition(10, 20), Direction.NONE);
    }

    void clearArrow(GamePosition start) {
        if (start.x() < 0 || start.y() < 0 || start.x() >= width || start.y() >= height)
            return;
        var arrow = arrows_map[start.x()][start.y()];
        arrows_map[start.x()][start.y()] = null;
        if (arrow != null)
            clearArrow(new GamePosition(start.x() + arrow.to.dx, start.y() + arrow.to.dy));
    }

    private int square(int a) {
        return a * a;
    }

    void makeArrow(GamePosition start, GamePosition end, Direction prev_dir) {
        if (start.x() < 0 || start.y() < 0 || start.x() >= width || start.y() >= height)
            return;
        int min_dist = 1000000;
        Direction min_dir = Direction.NONE;
        for (var dir : Direction.values()) {
            int dist = square(start.x() + dir.dx - end.x()) + square(start.y() + dir.dy - end.y());
            if (dist < min_dist) {
                min_dir = dir;
                min_dist = dist;
            }
        }
        arrows_map[start.x()][start.y()] = new Arrow(null, prev_dir, min_dir);
        if (min_dir != Direction.NONE)
            makeArrow(new GamePosition(start.x() + min_dir.dx, start.y() + min_dir.dy), end,
                      min_dir.getOpposite());
    }

    private ScreenPosition fromGameCoords(float game_x, float game_y) {
        float screen_x = -(game_y - game_x) * scale / 2 + offset.x();
        float screen_y = -(game_y + game_x) * scale / Tile.ASPECT_RATIO / 2 + offset.y();
        return new ScreenPosition(screen_x, screen_y);
    }

    private GamePosition fromScreenCoords(ScreenPosition pos) {
        float game_x =
                ((pos.x() - offset.x()) - Tile.ASPECT_RATIO * (pos.y() - offset.y())) / scale;
        float game_y =
                -((pos.x() - offset.x()) + Tile.ASPECT_RATIO * (pos.y() - offset.y())) / scale;
        return new GamePosition((int) Math.round(game_x), (int) Math.round(game_y));
    }

    public void update(ScreenPosition mouse) {
        var game_pos = fromScreenCoords(mouse);
        clearArrow(new GamePosition(0, 0));
        makeArrow(new GamePosition(0, 0), game_pos, Direction.NONE);
    }

    private void forAllVisibleTiles(float aspectRatio, Consumer<GamePosition> consumer) {
        int fromI = (int) Math.ceil(
                (-offset.x() + offset.y() * Tile.ASPECT_RATIO - aspectRatio * Tile.ASPECT_RATIO) /
                scale);
        int toI = (int) Math.ceil((-offset.x() + offset.y() * Tile.ASPECT_RATIO + 1) / scale);
        for (int i = Math.max(fromI - 1, 0); i < Math.min(toI + 1, width); i++) {
            int fromJ = (int) Math.ceil(Math.max((2 * offset.x() - 2) / scale + i,
                                                 2 * Tile.ASPECT_RATIO *
                                                 (offset.y() - aspectRatio) / scale - i));
            int toJ = (int) Math.ceil(Math.min(2 * offset.x() / scale + i,
                                               2 * Tile.ASPECT_RATIO * offset.y() / scale - i));
            for (int j = Math.max(fromJ - 1, 0); j < Math.min(toJ + 1, height); j++)
                consumer.accept(new GamePosition(i, j));
        }

    }

    public void setScale(ScreenPosition pivot, float new_scale) {
        offset = new ScreenPosition(pivot.x() - (pivot.x() - offset.x()) * (new_scale / scale),
                                    pivot.y() - (pivot.y() - offset.y()) * (new_scale / scale));
        scale = new_scale;
    }

    @Override
    public void draw(Drawer drawer) {
        var tiles = new ArrayList<Tile>();
        var arrows = new ArrayList<Top>();
        forAllVisibleTiles(drawer.getAspectRatio(), pos -> {
            var kind = (pos.x() + pos.y()) % 2 == 0 ? TileKind.DARK : TileKind.LIGHT;
            tiles.add(new Tile(kind, fromGameCoords(pos.x(), pos.y())));
            var arrow = arrows_map[pos.x()][pos.y()];
            if (arrow != null)
                arrows.add(new Arrow(fromGameCoords(pos.x(), pos.y()), arrow.from, arrow.to));
        });
        drawer.drawTiles(tiles, scale);
        drawer.drawTops(arrows, scale);
    }
}
