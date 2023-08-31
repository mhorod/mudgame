package io.game.world;

import io.game.Camera;
import io.game.GamePosition;
import io.game.WorldPosition;
import io.game.world.arrow.Arrow;
import io.game.world.arrow.ArrowKind;
import io.game.world.arrow.Direction;
import io.game.world.tile.Tile;
import io.game.world.tile.TileKind;
import io.game.world.unit.Unit;
import io.model.ScreenPosition;
import io.model.engine.Canvas;
import io.model.engine.TextureBank;

import java.util.ArrayList;

public class Map {
    Tile[][] tiles;
    ArrowKind[][] arrows;
    Unit unit = new Unit(new WorldPosition(2, 3, 1));
    private final int width, height;

    public Map(int width, int height) {
        this.width = width;
        this.height = height;
        tiles = new Tile[width][height];
        for (int x = 0; x < width; x++)
            for (int y = 0; y < height; y++)
                if (x <= 5) {
                    if ((x + y) % 2 == 0)
                        tiles[x][y] = new Tile(new GamePosition(x, y), TileKind.TILE_DARK);
                    else
                        tiles[x][y] = new Tile(new GamePosition(x, y), TileKind.TILE_LIGHT);
                } else {
                    tiles[x][y] = new Tile(new GamePosition(x, y), TileKind.FOG);
                }
        arrows = new ArrowKind[width][height];
        arrows[2][3] = new ArrowKind(Direction.NONE, Direction.NE);
        arrows[2][2] = new ArrowKind(Direction.SW, Direction.NE);
        arrows[2][1] = new ArrowKind(Direction.SW, Direction.NONE);
    }

    public void getEntityAt(ScreenPosition position, TextureBank textureBank, Camera camera) {
        System.out.println(unit.contains(position, textureBank, camera));
    }

    public void draw(Canvas canvas, Camera camera) {
        ArrayList<Tile> fogTiles = new ArrayList<>();
        camera.forAllVisibleTiles(canvas.getAspectRatio(), pos -> {
            if (pos.x() < 0 || pos.x() >= width || pos.y() < 0 || pos.y() >= height) return;
            var tile = tiles[pos.x()][pos.y()];
            if (tile.kind() == TileKind.FOG)
                fogTiles.add(tile);
            else
                tile.draw(canvas, camera);

            if (arrows[pos.x()][pos.y()] != null) {
                new Arrow(new GamePosition(pos.x(), pos.y()), arrows[pos.x()][pos.y()]).draw(canvas, camera);
            }
        });
        for (var tile : fogTiles)
            tile.draw(canvas, camera);
        unit.draw(canvas, camera);
    }
}
