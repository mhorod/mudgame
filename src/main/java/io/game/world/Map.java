package io.game.world;

import core.terrain.TerrainView;
import io.game.Camera;
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
    private final TerrainView terrain;

    public Map(TerrainView terrain) {
        this.terrain = terrain;
        arrows = new ArrowKind[terrain.size().width()][terrain.size().height()];
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
            switch (terrain.terrainAt(pos)) {
                case UNKNOWN -> new Tile(pos, TileKind.FOG).draw(canvas, camera);
                case WATER -> new Tile(pos, TileKind.TILE_LIGHT);
                case LAND -> new Tile(pos, TileKind.TILE_DARK).draw(canvas, camera);
            }

            if (pos.x() < 0 || pos.x() >= terrain.size().width() || pos.y() < 0 || pos.y() >= terrain.size().height())
                return;
            if (arrows[pos.x()][pos.y()] != null) {
                new Arrow(pos, arrows[pos.x()][pos.y()]).draw(canvas, camera);
            }
        });
        for (var tile : fogTiles)
            tile.draw(canvas, camera);
        unit.draw(canvas, camera);
    }
}
