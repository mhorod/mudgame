package io;

import java.util.List;

public interface Drawer {
    void drawTiles(List<Tile> tiles, float tile_width);
    void drawTops(List<Top> tops, float tile_width);
    float getAspectRatio();
}
