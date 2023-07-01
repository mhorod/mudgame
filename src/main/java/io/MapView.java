package io;

import java.util.ArrayList;
import java.util.function.Consumer;

public class MapView implements Drawable{
    private final int width;
    private final int height;

    public float scale;
    public ScreenPosition offset;

    public MapView(int width, int height, float scale, ScreenPosition offset) {
        this.width = width;
        this.height = height;
        this.scale = scale;
        this.offset = offset;
    }

    private ScreenPosition fromGameCoords(float game_x, float game_y) {
        float screen_x = -(game_y - game_x) * scale / 2 + offset.x();
        float screen_y = -(game_y + game_x) * scale / Tile.ASPECT_RATIO / 2 + offset.y();
        return new ScreenPosition(screen_x, screen_y);
    }

    private void forAllVisibleTiles(float aspectRatio, Consumer<Tile> consumer) {
        int fromI = (int) Math.ceil((-offset.x() + offset.y()*Tile.ASPECT_RATIO - aspectRatio * Tile.ASPECT_RATIO)/scale);
        int toI = (int) Math.ceil((-offset.x() + offset.y()*Tile.ASPECT_RATIO + 1)/scale);
        for (int i = Math.max(fromI - 1, 0); i < Math.min(toI + 1, width); i++) {
            int fromJ = (int) Math.ceil(Math.max(
                    (2 * offset.x() - 2) / scale + i,
                    2  * Tile.ASPECT_RATIO * (offset.y() - aspectRatio) / scale - i
            ));
            int toJ = (int) Math.ceil(Math.min(
                    2 * offset.x() / scale + i,
                    2  * Tile.ASPECT_RATIO * offset.y() / scale - i
            ));
            for (int j = Math.max(fromJ - 1, 0); j < Math.min(toJ + 1, height); j++) {
                var kind = (i + j) % 2 == 0 ? TileKind.DARK : TileKind.LIGHT;
                consumer.accept(new Tile(kind, fromGameCoords(i, j)));
            }
        }

    }

    public void setScale(ScreenPosition pivot, float new_scale) {
        offset = new ScreenPosition(
            pivot.x() - (pivot.x() - offset.x()) *  (new_scale / scale),
            pivot.y() - (pivot.y() - offset.y()) * (new_scale / scale)
        );
        scale = new_scale;
    }

    @Override
    public void draw(Drawer drawer) {
        var tiles = new ArrayList<Tile>();
        forAllVisibleTiles(drawer.getAspectRatio(), tiles::add);
        drawer.drawTiles(tiles, scale);
    }
}
