package io.game.world;

import core.entities.EntityBoardView;
import core.model.EntityID;
import core.model.Position;
import core.terrain.TerrainView;
import core.terrain.model.TerrainType;
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
    private final ArrayList<Position> path = new ArrayList<>();

    public Map(TerrainView terrain, EntityBoardView entities) {
        this.terrain = terrain;
        this.entities = entities;
        arrows = new ArrowKind[terrain.size().width()][terrain.size().height()];
    }

    public void setPath(List<Position> positions) {
        path.clear();
        path.addAll(positions);
    }

    private void setAnimation(EntityID entityID, EntityAnimation animation) {
        animation.init(entityFromID(entityID));
        animations.put(entityID, animation);
    }

    private Entity entityFromID(EntityID id) {
        if (animations.containsKey(id))
            return animations.get(id).getEntity();
        return new Entity(WorldPosition.from(entities.entityPosition(id)), id);
    }

    public void objectAt(ScreenPosition position, TextureBank textureBank, Camera camera, MapObserver listener) {
        var clickedEntity = entities.allEntities().stream()
                .map(core.entities.model.Entity::id)
                .map(this::entityFromID)
                .filter(entity -> entity.contains(position, textureBank, camera))
                .findFirst();
        if (clickedEntity.isPresent()) {
            listener.onEntity(clickedEntity.get().getId());
            return;
        }
        var tile = camera.getTile(position);
        if (terrain.terrainAt(tile) != TerrainType.VOID)
            listener.onTile(tile);
    }

    public void draw(Canvas canvas, Camera camera) {
        ArrayList<Tile> fogTiles = new ArrayList<>();
        ArrayList<Entity> entitiesToDraw = new ArrayList<>();
        camera.forAllVisibleTiles(canvas.getAspectRatio(), pos -> {
            switch (terrain.terrainAt(pos)) {
                case VOID -> fogTiles.add(new Tile(pos, TileKind.FOG));
                case WATER -> new Tile(pos, TileKind.TILE_LIGHT).draw(canvas, camera);
                case LAND, UNKNOWN -> new Tile(pos, TileKind.TILE_DARK).draw(canvas, camera);
            }


            entitiesToDraw.addAll(
                    entities.entitiesAt(pos).stream()
                            .filter(e -> !animations.containsKey(e.id()))
                            .map(e -> entityFromID(e.id()))
                            .toList()
            );
        });

        Arrow.fromPositions(path).forEach(arrow -> arrow.draw(canvas, camera));

        entitiesToDraw.addAll(animations.values().stream().map(EntityAnimation::getEntity).toList());
        entitiesToDraw.sort((a, b) -> {
            var valA = a.getPosition().x() + a.getPosition().y();
            var valB = b.getPosition().x() + b.getPosition().y();
            return (int) (100 * (valA - valB));
        });
        entitiesToDraw.forEach(entity -> entity.drawShadow(canvas, camera));
        fogTiles.forEach(fog -> fog.draw(canvas, camera));
        entitiesToDraw.forEach(entity -> entity.draw(canvas, camera));
    }

    @Override
    public void update(float deltaTime) {
        animations.values().forEach(animation -> animation.update(deltaTime));

        animations.entrySet().stream()
                .filter((entry) -> entry.getValue().finished())
                .toList()
                .forEach(entry -> animations.remove(entry.getKey()));
    }

    public void pickUp(EntityID entity) {
        setAnimation(entity, new AnimationChain(List.of(new Raise(), new Hover())));
    }

    public void putDown(EntityID entity) {
        setAnimation(entity, new Drop());
    }

    public void moveAlongPath(EntityID entity, List<Position> path) {
        setAnimation(entity, new AnimationChain(List.of(new Drop(), new MoveAlong(path))));
    }

    @Override
    public boolean finished() {
        return false;
    }
}
