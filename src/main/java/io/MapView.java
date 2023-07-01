package io;

import java.util.ArrayList;

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
    @Override
    public void draw(Drawer drawer) {
        var tiles = new ArrayList<Tile>();
        for (int i = 0; i < width; i++)
            for (int j = 0; j < height; j++) {
                var kind = (i + j) % 2 == 0 ? TileKind.DARK : TileKind.LIGHT;
                float x = -(j - i) * scale / 2 + offset.x();
                float y = -(j + i) * scale / Tile.ASPECT_RATIO / 2 + offset.y();
                tiles.add(new Tile(kind, x, y));
            }
        drawer.drawTiles(tiles, scale);
    }
}
