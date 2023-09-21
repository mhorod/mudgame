package io.game;

import core.model.Position;
import io.game.world.tile.Tile;
import io.model.ScreenPosition;

import java.util.function.Consumer;

public class Camera {
    private float tileWidth = 0.5f;
    private float aspectRatio = 1f;
    public float offsetX, offsetY;

    public float getTileWidth() {
        return tileWidth;
    }

    void setZoom(ScreenPosition pivot, float zoom) {
        offsetX = pivot.x() - (pivot.x() - offsetX) * zoom / tileWidth;
        offsetY = pivot.y() - (pivot.y() - offsetY) * zoom / tileWidth;
        tileWidth = zoom;
    }

    public void forAllVisibleTiles(Consumer<Position> f) {
        int fromI = (int) Math.ceil((-offsetX + offsetY * Tile.ASPECT_RATIO - aspectRatio * Tile.ASPECT_RATIO) / tileWidth);
        int toI = (int) Math.ceil((-offsetX + offsetY * Tile.ASPECT_RATIO + 1) / tileWidth);
        for (int i = fromI - 1; i < toI + 1; i++) {
            int fromJ = (int) Math.ceil(Math.max(
                    (2 * offsetX - 2) / tileWidth + i,
                    2 * Tile.ASPECT_RATIO * (offsetY - aspectRatio) / tileWidth - i
            ));
            int toJ = (int) Math.ceil(Math.min(
                    2 * offsetX / tileWidth + i,
                    2 * Tile.ASPECT_RATIO * offsetY / tileWidth - i
            ));
            for (int j = fromJ - 1; j < toJ + 1; j++)
                f.accept(new Position(i, j));
        }
    }

    public void setAspectRatio(float aspectRatio) {
        this.aspectRatio = aspectRatio;
    }

    public ScreenPosition convert(WorldPosition pos) {
        float screenX = -(pos.y() - pos.x()) * tileWidth / 2 + offsetX;
        float screenY = -(pos.y() + pos.x() - pos.z()) * tileWidth / Tile.ASPECT_RATIO / 2 + offsetY;
        return new ScreenPosition(screenX, screenY);
    }

    public Position getTile(ScreenPosition pos) {
        var y = Math.round(-((pos.x() - offsetX) + (pos.y() - offsetY) * Tile.ASPECT_RATIO) / tileWidth);
        var x = Math.round(((pos.x() - offsetX) - (pos.y() - offsetY) * Tile.ASPECT_RATIO) / tileWidth);
        return new Position(x, y);
    }

}
