package io.game.world;

import core.entities.EntityBoardView;
import core.model.EntityID;
import core.model.Position;
import core.terrain.TerrainView;
import io.animation.Animation;
import io.game.Camera;
import io.game.WorldPosition;
import io.game.world.arrow.Arrow;
import io.game.world.arrow.ArrowKind;
import io.game.world.entity.*;
import io.game.world.tile.Tile;
import io.game.world.tile.TileKind;
import io.model.ScreenPosition;
import io.model.engine.Canvas;
import io.model.engine.TextureBank;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Map implements Animation {
    ArrowKind[][] arrows;
    HashMap<EntityID, EntityAnimation> animations = new HashMap<>();
    private final TerrainView terrain;
    private final EntityBoardView entities;

    public Map(TerrainView terrain, EntityBoardView entities) {
        this.terrain = terrain;
        this.entities = entities;
        arrows = new ArrowKind[terrain.size().width()][terrain.size().height()];
        setAnimation(new EntityID(1), new AnimationChain(List.of(
                new Raise(), new Drop(),
                new MoveAlong(List.of(
                        new Position(2, 2),
                        new Position(2, 1),
                        new Position(1, 1),
                        new Position(1, 2),
                        new Position(2, 2)
                ))
        )));
    }

    private void setAnimation(EntityID entityID, EntityAnimation animation) {
        animation.init(entityFromID(entityID));
        animations.put(entityID, animation);
    }

    private Entity entityFromID(EntityID id) {
        if (animations.containsKey(id))
            return animations.get(id).getEntity();
        return new Entity(WorldPosition.from(entities.entityPosition(id)));
    }

    public void getEntityAt(ScreenPosition position, TextureBank textureBank, Camera camera) {
    }

    public void draw(Canvas canvas, Camera camera) {
        ArrayList<Tile> fogTiles = new ArrayList<>();
        ArrayList<Entity> entitiesToDraw = new ArrayList<>();
        camera.forAllVisibleTiles(canvas.getAspectRatio(), pos -> {
            switch (terrain.terrainAt(pos)) {
                case UNKNOWN -> fogTiles.add(new Tile(pos, TileKind.FOG));
                case WATER -> new Tile(pos, TileKind.TILE_LIGHT);
                case LAND -> new Tile(pos, TileKind.TILE_DARK).draw(canvas, camera);
            }


            entitiesToDraw.addAll(
                    entities.entitiesAt(pos).stream()
                            .filter(e -> !animations.containsKey(e.id()))
                            .map(e -> new Entity(WorldPosition.from(pos)))
                            .toList()
            );

            if (pos.x() < 0 || pos.x() >= terrain.size().width() || pos.y() < 0 || pos.y() >= terrain.size().height())
                return;
            if (arrows[pos.x()][pos.y()] != null) {
                new Arrow(pos, arrows[pos.x()][pos.y()]).draw(canvas, camera);
            }
        });
        entitiesToDraw.addAll(animations.values().stream().map(EntityAnimation::getEntity).toList());
        entitiesToDraw.sort((a, b) -> {
            var valA = a.getPosition().x() + a.getPosition().y();
            var valB = b.getPosition().x() + b.getPosition().y();
            return (int) (100 * (valA - valB));
        });
        for (var entity : entitiesToDraw)
            entity.draw(canvas, camera);
//        for (var tile : fogTiles)
//            tile.draw(canvas, camera);
    }

    @Override
    public void update(float deltaTime) {
        animations.values().forEach(animation -> animation.update(deltaTime));

        animations.entrySet().stream()
                .filter((entry) -> entry.getValue().finished())
                .toList()
                .forEach(entry -> animations.remove(entry.getKey()));
    }

    @Override
    public boolean finished() {
        return false;
    }
}
